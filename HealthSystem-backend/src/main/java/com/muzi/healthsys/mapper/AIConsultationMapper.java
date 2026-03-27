package com.muzi.healthsys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.healthsys.entity.AIConsultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI健康咨询Mapper接口
 *
 * @author MuZi
 * @since 2025-01-XX
 */
@Mapper
public interface AIConsultationMapper extends BaseMapper<AIConsultation> {
    
    /**
     * 根据关键词匹配问答
     * @param keyword 关键词
     * @return 匹配的问答列表，按优先级降序
     */
    @Select("SELECT * FROM j_ai_qa WHERE keywords LIKE CONCAT('%', #{keyword}, '%') ORDER BY priority DESC, id DESC")
    List<AIConsultation> findByKeyword(@Param("keyword") String keyword);
}

