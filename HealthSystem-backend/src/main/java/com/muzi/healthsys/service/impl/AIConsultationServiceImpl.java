package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muzi.healthsys.entity.AIAdviceTemplate;
import com.muzi.healthsys.entity.AIConsultation;
import com.muzi.healthsys.entity.AIRedFlagDict;
import com.muzi.healthsys.entity.AISymptomSlotTemplate;
import com.muzi.healthsys.entity.AITriageRule;
import com.muzi.healthsys.mapper.AIAdviceTemplateMapper;
import com.muzi.healthsys.mapper.AIConsultationMapper;
import com.muzi.healthsys.mapper.AIRedFlagDictMapper;
import com.muzi.healthsys.mapper.AISymptomSlotTemplateMapper;
import com.muzi.healthsys.mapper.AITriageRuleMapper;
import com.muzi.healthsys.service.IAIConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AIConsultationServiceImpl extends ServiceImpl<AIConsultationMapper, AIConsultation>
        implements IAIConsultationService {

    private static final Map<String, List<String>> SYNONYM_MAP = new HashMap<>();
    private static final Set<String> POLICY_KEYWORDS = new HashSet<>(Arrays.asList(
            "医保", "报销", "参保", "门诊", "住院", "起付线", "封顶线", "比例", "材料", "流程", "校医院", "定点"
    ));

    private static final Set<String> SYMPTOM_KEYWORDS = new HashSet<>(Arrays.asList(
            "痛", "疼", "发热", "咳", "头晕", "恶心", "腹泻", "胸闷", "呼吸", "失眠", "不舒服", "症状", "哪里"
    ));

    private static final Map<String, Map<String, Object>> SESSION_STORE = new ConcurrentHashMap<>();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private AISymptomSlotTemplateMapper slotTemplateMapper;
    @Autowired
    private AIRedFlagDictMapper redFlagDictMapper;
    @Autowired
    private AITriageRuleMapper triageRuleMapper;
    @Autowired
    private AIAdviceTemplateMapper adviceTemplateMapper;
    @Autowired
    private LlmClientService llmClientService;

    static {
        SYNONYM_MAP.put("健身", Arrays.asList("运动", "锻炼", "健身", "训练", "健身运动"));
        SYNONYM_MAP.put("运动", Arrays.asList("健身", "锻炼", "运动", "训练", "健身运动"));
        SYNONYM_MAP.put("锻炼", Arrays.asList("健身", "运动", "锻炼", "训练", "健身运动"));

        SYNONYM_MAP.put("减肥", Arrays.asList("减重", "瘦身", "瘦下来", "减肥"));
        SYNONYM_MAP.put("减重", Arrays.asList("减肥", "瘦身", "瘦下来", "减重"));
        SYNONYM_MAP.put("瘦身", Arrays.asList("减肥", "减重", "瘦下来", "瘦身"));

        SYNONYM_MAP.put("饮食", Arrays.asList("营养", "健康饮食", "饮食", "膳食"));
        SYNONYM_MAP.put("营养", Arrays.asList("饮食", "健康饮食", "营养", "膳食"));

        SYNONYM_MAP.put("睡眠", Arrays.asList("失眠", "睡不着", "睡眠", "休息"));
        SYNONYM_MAP.put("失眠", Arrays.asList("睡眠", "睡不着", "失眠", "休息"));
    }

    @Override
    public String getAnswer(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "您好，请描述您的健康问题，我会尽力为您提供建议。";
        }
        return matchAnswerFromKnowledge(question);
    }

    @Override
    public String startSession(Integer userId) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> state = new HashMap<>();
        state.put("userId", userId);
        state.put("intent", "UNKNOWN");
        state.put("slots", new HashMap<String, String>());
        SESSION_STORE.put(sessionId, state);
        return sessionId;
    }

    @Override
    public Map<String, Object> chat(String sessionId, Integer userId, String question) {
        Map<String, Object> result = new HashMap<>();
        if (question == null || question.trim().isEmpty()) {
            result.put("reply", "请先描述您的问题，我会一步步帮您分析。");
            result.put("intent", "UNKNOWN");
            return result;
        }

        Map<String, Object> state = SESSION_STORE.computeIfAbsent(sessionId, k -> {
            Map<String, Object> init = new HashMap<>();
            init.put("userId", userId);
            init.put("intent", "UNKNOWN");
            init.put("slots", new HashMap<String, String>());
            return init;
        });

        String previousIntent = (String) state.get("intent");
        String intent = routeIntent(question, previousIntent);

        // 意图发生切换时，重置症状槽位，避免政策问题被上一轮症状上下文污染
        if (previousIntent != null && !previousIntent.equals(intent)) {
            state.put("slots", new HashMap<String, String>());
        }

        state.put("intent", intent);

        if ("POLICY_QUERY".equals(intent)) {
            return handlePolicyQuery(question, result);
        }

        if ("SYMPTOM_TRIAGE".equals(intent)) {
            @SuppressWarnings("unchecked")
            Map<String, String> slots = (Map<String, String>) state.get("slots");
            fillSlots(slots, question);
            return handleSymptomTriage(slots, result);
        }

        result.put("intent", "GENERAL_QA");
        result.put("reply", matchAnswerFromKnowledge(question));
        result.put("action", "general_reply");
        return result;
    }

    private String routeIntent(String question, String currentIntent) {
        // 政策问题优先，确保“门诊报销多少”不会被症状会话上下文劫持
        boolean hasPolicy = POLICY_KEYWORDS.stream().anyMatch(question::contains);
        if (hasPolicy) return "POLICY_QUERY";

        // 若当前正在症状分诊，且用户本轮不是明确政策问题，则继续分诊
        if ("SYMPTOM_TRIAGE".equals(currentIntent)) {
            boolean hasSymptom = SYMPTOM_KEYWORDS.stream().anyMatch(question::contains);
            if (hasSymptom || looksLikeSlotAnswer(question)) {
                return "SYMPTOM_TRIAGE";
            }
        }

        boolean hasSymptom = SYMPTOM_KEYWORDS.stream().anyMatch(question::contains);
        if (hasSymptom) return "SYMPTOM_TRIAGE";

        return "GENERAL_QA";
    }

    private Map<String, Object> handlePolicyQuery(String question, Map<String, Object> result) {
        String answer = matchAnswerFromKnowledge(question);
        result.put("intent", "POLICY_QUERY");
        result.put("action", "policy_navigation");

        List<Map<String, String>> citations = new ArrayList<>();
        Map<String, String> c1 = new HashMap<>();
        c1.put("title", "校医院医保政策知识库");
        c1.put("summary", "根据政策问答知识库与Dify检索增强生成解读，具体以学校/医保局最新公告为准。");
        c1.put("url", "/policy/docs");
        citations.add(c1);
        result.put("policyCitations", citations);

        String fallback = "【政策解读】\n" + answer + "\n\n如需精确判断，请补充：门诊/住院、就诊机构是否定点、是否已参保。";
        Map<String, Object> llmResult = llmClientService.enhancePolicyAnswer(question, fallback, citations, null, null);
        result.put("reply", llmResult.getOrDefault("reply", fallback));
        result.put("provider", llmResult.getOrDefault("provider", "rule"));
        result.put("conversationId", llmResult.getOrDefault("conversationId", ""));
        return result;
    }

    private Map<String, Object> handleSymptomTriage(Map<String, String> slots, Map<String, Object> result) {
        result.put("intent", "SYMPTOM_TRIAGE");

        List<AISymptomSlotTemplate> templateList = getEnabledSlotTemplates();
        for (AISymptomSlotTemplate t : templateList) {
            if (t.getRequiredFlag() != null && t.getRequiredFlag() == 1) {
                String value = slots.get(t.getSlotCode());
                if (value == null || value.trim().isEmpty()) {
                    result.put("reply", t.getQuestionPrompt());
                    result.put("nextSlot", t.getSlotCode());
                    result.put("action", "ask_followup");
                    return result;
                }
            }
        }

        AIRedFlagDict redFlag = matchRedFlag(slots);
        if (redFlag != null) {
            result.put("triageLevel", redFlag.getEmergencyLevel());
            result.put("action", "visit_clinic");
            result.put("reply", redFlag.getAdviceText() + "\n\n【提示】本系统建议仅供参考，不替代医生诊断。");
            result.put("matchedRule", redFlag.getFlagName());
            return result;
        }

        AITriageRule rule = matchTriageRule(slots);
        if (rule != null) {
            result.put("triageLevel", rule.getTriageLevel());
            result.put("action", rule.getActionCode());
            String advice = getAdvice(rule.getTriageLevel(), rule.getActionCode(), rule.getAdviceText());
            result.put("reply", advice + "\n\n【提示】本系统建议仅供参考，不替代医生诊断。");
            result.put("matchedRule", rule.getRuleName());
            result.put("slots", slots);
            return result;
        }

        result.put("triageLevel", "L2");
        result.put("action", "online_consult");
        result.put("reply", "目前信息不足以精确分级，建议先在线问诊，由医生进一步评估。\n\n【提示】本系统建议仅供参考，不替代医生诊断。");
        result.put("slots", slots);
        return result;
    }

    private List<AISymptomSlotTemplate> getEnabledSlotTemplates() {
        LambdaQueryWrapper<AISymptomSlotTemplate> w = new LambdaQueryWrapper<>();
        w.eq(AISymptomSlotTemplate::getStatus, 1);
        w.orderByAsc(AISymptomSlotTemplate::getSortNo);
        return slotTemplateMapper.selectList(w);
    }

    private AIRedFlagDict matchRedFlag(Map<String, String> slots) {
        LambdaQueryWrapper<AIRedFlagDict> w = new LambdaQueryWrapper<>();
        w.eq(AIRedFlagDict::getStatus, 1);
        w.orderByDesc(AIRedFlagDict::getPriority);
        List<AIRedFlagDict> list = redFlagDictMapper.selectList(w);
        String corpus = buildSlotCorpus(slots);

        for (AIRedFlagDict item : list) {
            List<String> patterns = splitByComma(item.getSymptomPattern());
            String logic = item.getLogicType() == null ? "ANY" : item.getLogicType().toUpperCase(Locale.ROOT);
            boolean hit;
            if ("ALL".equals(logic)) {
                hit = patterns.stream().allMatch(corpus::contains);
            } else {
                hit = patterns.stream().anyMatch(corpus::contains);
            }
            if (hit) return item;
        }
        return null;
    }

    private AITriageRule matchTriageRule(Map<String, String> slots) {
        LambdaQueryWrapper<AITriageRule> w = new LambdaQueryWrapper<>();
        w.eq(AITriageRule::getStatus, 1);
        w.orderByDesc(AITriageRule::getPriority);
        List<AITriageRule> rules = triageRuleMapper.selectList(w);

        for (AITriageRule rule : rules) {
            if (evaluateRule(rule.getConditionJson(), slots)) {
                return rule;
            }
        }
        return null;
    }

    private boolean evaluateRule(String conditionJson, Map<String, String> slots) {
        if (conditionJson == null || conditionJson.trim().isEmpty()) return false;
        try {
            JsonNode root = OBJECT_MAPPER.readTree(conditionJson);
            String corpus = buildSlotCorpus(slots);

            if (root.has("severity")) {
                int sev = parseSeverity(slots.get("severity"));
                JsonNode s = root.get("severity");
                if (s.has("gte") && sev < s.get("gte").asInt()) return false;
                if (s.has("lte") && sev > s.get("lte").asInt()) return false;
            }

            if (root.has("duration_hours")) {
                int hours = parseDurationHours(slots.get("duration"));
                JsonNode d = root.get("duration_hours");
                if (d.has("gte") && hours < d.get("gte").asInt()) return false;
                if (d.has("lte") && hours > d.get("lte").asInt()) return false;
            }

            if (root.has("exclude_keywords") && root.get("exclude_keywords").isArray()) {
                for (JsonNode kw : root.get("exclude_keywords")) {
                    if (corpus.contains(kw.asText())) return false;
                }
            }

            if (root.has("or_keywords") && root.get("or_keywords").isArray()) {
                boolean hasOne = false;
                for (JsonNode kw : root.get("or_keywords")) {
                    if (corpus.contains(kw.asText())) {
                        hasOne = true;
                        break;
                    }
                }
                if (!hasOne) return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getAdvice(String level, String actionCode, String fallback) {
        LambdaQueryWrapper<AIAdviceTemplate> w = new LambdaQueryWrapper<>();
        w.eq(AIAdviceTemplate::getStatus, 1);
        w.eq(AIAdviceTemplate::getTriageLevel, level);
        w.eq(AIAdviceTemplate::getActionCode, actionCode);
        w.last("LIMIT 1");
        AIAdviceTemplate t = adviceTemplateMapper.selectOne(w);
        if (t != null && t.getContent() != null && !t.getContent().trim().isEmpty()) {
            return t.getContent();
        }
        return fallback == null || fallback.trim().isEmpty() ? "建议及时就医评估。" : fallback;
    }

    private String buildSlotCorpus(Map<String, String> slots) {
        return String.join(" ", slots.values()).toLowerCase(Locale.ROOT);
    }

    private List<String> splitByComma(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    private void fillSlots(Map<String, String> slots, String question) {
        List<AISymptomSlotTemplate> templateList = getEnabledSlotTemplates();

        for (AISymptomSlotTemplate t : templateList) {
            String code = t.getSlotCode();
            if (slots.containsKey(code) && slots.get(code) != null && !slots.get(code).trim().isEmpty()) {
                continue;
            }
            String value = detectBySlotCode(code, question);
            if (value != null && !value.trim().isEmpty()) {
                slots.put(code, value);
            }
        }
    }

    private String detectBySlotCode(String slotCode, String text) {
        if ("body_part".equals(slotCode) || "bodyPart".equals(slotCode)) {
            return detectBodyPart(text);
        }
        if ("symptom_nature".equals(slotCode) || "painType".equals(slotCode)) {
            return detectPainType(text);
        }
        if ("duration".equals(slotCode)) {
            return detectDuration(text);
        }
        if ("severity".equals(slotCode)) {
            return detectSeverity(text);
        }
        if ("accompanying".equals(slotCode) || "accompany".equals(slotCode)) {
            return detectAccompany(text);
        }
        if ("history".equals(slotCode)) {
            return detectHistory(text);
        }
        return null;
    }

    private int parseSeverity(String severityText) {
        if (severityText == null) return 0;
        String cleaned = severityText.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) return 0;
        try {
            int value = Integer.parseInt(cleaned);
            if (value < 0) return 0;
            return Math.min(value, 10);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseDurationHours(String durationText) {
        if (durationText == null || durationText.trim().isEmpty()) return 0;
        String s = durationText;
        String numText = s.replaceAll("[^0-9]", "");
        int num = 0;
        try {
            if (!numText.isEmpty()) num = Integer.parseInt(numText);
        } catch (Exception ignored) {
        }
        if (s.contains("周")) return Math.max(1, num) * 24 * 7;
        if (s.contains("天")) return Math.max(1, num) * 24;
        if (s.contains("小时") || s.contains("h") || s.contains("H")) return Math.max(1, num);
        if (s.contains("月")) return Math.max(1, num) * 24 * 30;
        if (s.contains("一直") || s.contains("反复")) return 24 * 7;
        return Math.max(1, num);
    }

    private String detectBodyPart(String text) {
        String[] bodyParts = {"头", "咽喉", "喉咙", "胸", "腹", "胃", "腰", "背", "腿", "手", "眼", "耳"};
        for (String bp : bodyParts) if (text.contains(bp)) return bp;
        return null;
    }

    private String detectPainType(String text) {
        String[] painTypes = {"刺痛", "钝痛", "闷痛", "灼痛", "胀痛", "酸痛", "疼"};
        for (String pt : painTypes) if (text.contains(pt)) return pt;
        return null;
    }

    private String detectDuration(String text) {
        String[] durationHints = {"小时", "天", "周", "月", "一直", "反复"};
        for (String d : durationHints) if (text.contains(d)) return text;
        return null;
    }

    private String detectSeverity(String text) {
        if (text.matches(".*([1-9]|10)分.*")) return text;
        return null;
    }

    private String detectAccompany(String text) {
        String[] accompanies = {"发热", "发烧", "呕吐", "呼吸困难", "胸闷", "意识", "抽搐", "腹泻", "咳嗽", "便血", "呕血"};
        List<String> found = new ArrayList<>();
        for (String a : accompanies) if (text.contains(a)) found.add(a);
        return found.isEmpty() ? null : String.join("、", found);
    }

    private String detectHistory(String text) {
        String[] history = {"高血压", "糖尿病", "过敏", "手术", "慢性病", "哮喘"};
        List<String> found = new ArrayList<>();
        for (String h : history) if (text.contains(h)) found.add(h);
        return found.isEmpty() ? null : String.join("、", found);
    }

    /**
     * 判断用户输入是否像“槽位回答”（如：2天、6分、有发热）
     */
    private boolean looksLikeSlotAnswer(String text) {
        if (text == null || text.trim().isEmpty()) return false;

        if (text.matches(".*([1-9]|10)分.*")) return true;
        if (text.contains("小时") || text.contains("天") || text.contains("周") || text.contains("月") || text.contains("反复") || text.contains("一直")) return true;

        String[] bodyParts = {"头", "咽喉", "喉咙", "胸", "腹", "胃", "腰", "背", "腿", "手", "眼", "耳"};
        for (String bp : bodyParts) if (text.contains(bp)) return true;

        String[] painTypes = {"刺痛", "钝痛", "闷痛", "灼痛", "胀痛", "酸痛", "疼"};
        for (String p : painTypes) if (text.contains(p)) return true;

        String[] accompanies = {"发热", "发烧", "呕吐", "呼吸困难", "胸闷", "意识", "抽搐", "腹泻", "咳嗽", "便血", "呕血"};
        for (String a : accompanies) if (text.contains(a)) return true;

        return false;
    }

    private String matchAnswerFromKnowledge(String question) {
        Set<String> keywords = extractKeywords(question);

        List<AIConsultation> matchedQA = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();

        for (String keyword : keywords) {
            if (keyword.length() >= 2) {
                List<AIConsultation> results = baseMapper.findByKeyword(keyword);
                if (results != null && !results.isEmpty()) {
                    for (AIConsultation qa : results) {
                        if (qa.getId() != null && !addedIds.contains(qa.getId())) {
                            matchedQA.add(qa);
                            addedIds.add(qa.getId());
                        }
                    }
                }

                List<String> synonyms = getSynonyms(keyword);
                for (String synonym : synonyms) {
                    if (!synonym.equals(keyword)) {
                        List<AIConsultation> synonymResults = baseMapper.findByKeyword(synonym);
                        if (synonymResults != null && !synonymResults.isEmpty()) {
                            for (AIConsultation qa : synonymResults) {
                                if (qa.getId() != null && !addedIds.contains(qa.getId())) {
                                    matchedQA.add(qa);
                                    addedIds.add(qa.getId());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!matchedQA.isEmpty()) {
            matchedQA = matchedQA.stream()
                    .sorted((a, b) -> {
                        int priorityA = a.getPriority() != null ? a.getPriority() : 0;
                        int priorityB = b.getPriority() != null ? b.getPriority() : 0;
                        int priorityCompare = Integer.compare(priorityB, priorityA);
                        if (priorityCompare != 0) {
                            return priorityCompare;
                        }
                        int idA = a.getId() != null ? a.getId() : 0;
                        int idB = b.getId() != null ? b.getId() : 0;
                        return Integer.compare(idB, idA);
                    })
                    .collect(Collectors.toList());

            return matchedQA.get(0).getAnswer();
        }

        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIConsultation::getCategory, "其他");
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.last("LIMIT 1");
        AIConsultation defaultQA = this.getOne(wrapper);

        if (defaultQA != null) {
            return defaultQA.getAnswer();
        }

        return "感谢您的提问。建议您咨询专业医生或健康顾问，以获得更准确的建议。";
    }

    private Set<String> extractKeywords(String question) {
        Set<String> keywords = new HashSet<>();
        String cleaned = question.replaceAll("[，。！？、；：,.\"!?;:（）()【】\\[\\]《》<>]", "");

        for (int len = 2; len <= 4 && len <= cleaned.length(); len++) {
            for (int i = 0; i <= cleaned.length() - len; i++) {
                String keyword = cleaned.substring(i, i + len);
                if (!isCommonQuestionWord(keyword)) {
                    keywords.add(keyword);
                }
            }
        }

        if (keywords.isEmpty() && cleaned.length() > 0) {
            keywords.add(cleaned);
        }
        return keywords;
    }

    private boolean isCommonQuestionWord(String word) {
        String[] questionWords = {"如何", "怎样", "怎么", "什么", "哪些", "哪个", "为什么", "为何", "能否", "可以", "应该", "需要"};
        for (String qw : questionWords) {
            if (qw.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getSynonyms(String keyword) {
        List<String> synonyms = new ArrayList<>();
        synonyms.add(keyword);

        for (Map.Entry<String, List<String>> entry : SYNONYM_MAP.entrySet()) {
            if (entry.getValue().contains(keyword)) {
                synonyms.addAll(entry.getValue());
            }
        }

        return synonyms.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<AIConsultation> getAllQA() {
        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.orderByDesc(AIConsultation::getId);
        return this.list(wrapper);
    }
}
