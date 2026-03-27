package com.muzi.healthsys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.Detail;
import com.muzi.healthsys.entity.SportInfo;
import com.muzi.healthsys.service.IDetailService;
import com.muzi.healthsys.service.ISportInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/detail")
public class DetailController {

    private static final Logger logger = LoggerFactory.getLogger(DetailController.class);

    @Autowired
    private IDetailService detailService;

    @Autowired
    private ISportInfoService sportInfoService;




    @GetMapping("/DetailInfo/{sportName}")
    public Unification<Detail> getDetailInfo(@PathVariable String sportName) {
        List<Detail> detailList = detailService.getDetailInfo(sportName);

        System.out.println(detailList);
        if (detailList == null || detailList.isEmpty()) {
            return Unification.fail("查询结果为空");
        }
        // 如果只查询到一条结果，可以直接返回
        Detail detail = detailList.get(0);
        return Unification.success(detail);
    }



    @GetMapping("/getDetailList")
    public Unification<Map<String,Object>> getDetailList(@RequestParam(value = "sportType", required = false) String sportType,
                                                         @RequestParam("pageNo") Long pageNo,
                                                         @RequestParam("pageSize") Long pageSize) {

        LambdaQueryWrapper<Detail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(sportType), Detail::getSportType, sportType); // 如果sportType参数不为空，则添加运动类型查询条件
        Page<Detail> page = new Page<>(pageNo, pageSize); // 构建分页对象，指定页码和每页大小

        detailService.page(page, wrapper); //查询指定页码、每页大小和查询条件的用户列表
        Map<String, Object> data = new HashMap<>();

        data.put("total", page.getTotal()); // 用户总数
        data.put("rows", page.getRecords()); // 用户列表
        System.out.println(data);
        return Unification.success(data); // 返回成功响应和响应数据
    }




    @PostMapping("/addDetail")
    public Unification<?> addDetail(@RequestBody Detail detail) {
        boolean isSuccess = detailService.addDetail(detail);
        if (isSuccess) {
            return Unification.success("新增成功");
        } else {
            return Unification.fail("新增失败，运动类型已存在");
        }
    }



    @PutMapping("/updateDetail")
    public Unification<?> updateDetail(@RequestBody Detail detail){
        detailService.updateDetail(detail);
        return Unification.success("修改成功");
    }


    @GetMapping("/getDetailById/{id}")
    public Unification<Detail> getDetailById(@PathVariable("id") Integer id){
        // 通过用户id调用userService的getUserById方法获取用户信息
        Detail detail = detailService.getDetailById(id);
        // 将获取到的用户信息封装成Unification类型并返回
        return  Unification.success(detail);
    }

    @GetMapping("/getDetailBySportInfoId/{sportInfoId}")
    public Unification<Detail> getDetailBySportInfoId(@PathVariable("sportInfoId") Integer sportInfoId){
        Detail detail = detailService.getDetailBySportInfoId(sportInfoId);
        if (detail == null) {
            return Unification.fail("未找到对应的运动详情");
        }
        return Unification.success(detail);
    }


    @DeleteMapping("/deleteDetailById/{id}")
    public Unification<Detail> deleteDetailById(@PathVariable("id") Integer id){
        detailService.deletDetailById(id);
        return  Unification.success("删除成功");
    }

    /**
     * 获取所有运动类型列表，用于前端下拉框
     */
    @GetMapping("/getSportInfoList")
    public Unification<List<SportInfo>> getSportInfoList(){
        List<SportInfo> sportInfoList = sportInfoService.getAllSportInfos();
        return Unification.success(sportInfoList);
    }

    /**
     * 上传运动详情图片
     */
    @PostMapping("/uploadImage")
    public Unification<String> uploadImage(@RequestParam("file") MultipartFile file) {
        logger.info("收到图片上传请求，文件名：{}，文件大小：{}", 
            file != null ? file.getOriginalFilename() : "null",
            file != null ? file.getSize() : 0);
        
        try {
            if (file == null || file.isEmpty()) {
                logger.warn("上传文件为空");
                return Unification.fail("上传文件不能为空");
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Unification.fail("文件名不能为空");
            }
            
            // 获取文件扩展名（不区分大小写）
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
                return Unification.fail("文件格式不正确，缺少扩展名");
            }
            
            String suffix = originalFilename.substring(lastDotIndex).toLowerCase();
            if (!suffix.matches(".(jpg|jpeg|png|gif|bmp|webp)$")) {
                return Unification.fail("只支持图片格式：jpg、jpeg、png、gif、bmp、webp");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Unification.fail("图片大小不能超过10MB");
            }
            
            // 创建上传目录（使用绝对路径，基于项目根目录）
            String projectRoot = System.getProperty("user.dir");
            logger.info("项目根目录：{}", projectRoot);
            
            // 使用Paths API，更安全，能处理路径中的空格
            Path uploadPath = Paths.get(projectRoot, "upload", "img");
            File dir = uploadPath.toFile();
            
            // 确保目录存在，如果不存在则创建（使用Files API，更健壮）
            if (!dir.exists()) {
                logger.info("上传目录不存在，开始创建，目录路径：{}", uploadPath.toAbsolutePath());
                try {
                    Files.createDirectories(uploadPath);
                    logger.info("创建上传目录成功，目录路径：{}", uploadPath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("创建上传目录失败，目录路径：{}，错误信息：{}", 
                        uploadPath.toAbsolutePath(), e.getMessage(), e);
                    return Unification.fail("创建上传目录失败，请检查权限：" + e.getMessage());
                }
            }
            
            // 再次确认目录存在且是目录
            if (!dir.exists() || !dir.isDirectory()) {
                logger.error("上传目录不存在或不是目录，目录路径：{}，存在：{}，是目录：{}", 
                    uploadPath.toAbsolutePath(), dir.exists(), dir.isDirectory());
                return Unification.fail("上传目录不存在，请检查权限");
            }
            
            // 检查目录是否可写
            if (!dir.canWrite()) {
                logger.error("上传目录不可写，目录路径：{}", uploadPath.toAbsolutePath());
                return Unification.fail("上传目录不可写，请检查权限");
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            Path filePath = uploadPath.resolve(fileName);
            logger.info("文件保存路径：{}", filePath.toAbsolutePath());
            
            // 确保父目录存在（双重检查，防止并发问题）
            Path parentPath = filePath.getParent();
            if (parentPath != null && !Files.exists(parentPath)) {
                logger.warn("父目录不存在，重新创建，父目录路径：{}", parentPath.toAbsolutePath());
                try {
                    Files.createDirectories(parentPath);
                    logger.info("父目录创建成功，父目录路径：{}", parentPath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("创建父目录失败，父目录路径：{}，错误信息：{}", 
                        parentPath.toAbsolutePath(), e.getMessage(), e);
                    return Unification.fail("创建父目录失败，请检查权限：" + e.getMessage());
                }
            }
            
            // 再次确认父目录存在
            if (parentPath != null && (!Files.exists(parentPath) || !Files.isDirectory(parentPath))) {
                logger.error("父目录不存在或不是目录，父目录路径：{}，存在：{}，是目录：{}", 
                    parentPath.toAbsolutePath(), Files.exists(parentPath), 
                    Files.exists(parentPath) ? Files.isDirectory(parentPath) : false);
                return Unification.fail("父目录不存在，请检查权限");
            }
            
            // 保存文件（使用Files API，更安全）
            File dest = filePath.toFile();
            try {
                Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                logger.info("文件保存成功，文件路径：{}，文件大小：{}", filePath.toAbsolutePath(), dest.length());
            } catch (IOException e) {
                logger.error("文件保存IO异常，文件路径：{}，错误信息：{}", filePath.toAbsolutePath(), e.getMessage(), e);
                // 如果文件已创建但保存失败，尝试删除
                if (dest.exists()) {
                    try {
                        Files.delete(filePath);
                        logger.warn("已删除失败的文件");
                    } catch (IOException deleteException) {
                        logger.warn("删除失败的文件时出错：{}", deleteException.getMessage());
                    }
                }
                return Unification.fail("文件保存失败：" + e.getMessage());
            }
            
            // 验证文件是否成功保存
            if (!dest.exists() || dest.length() == 0) {
                logger.error("文件保存验证失败，文件路径：{}，存在：{}，大小：{}", 
                    filePath.toAbsolutePath(), dest.exists(), dest.exists() ? dest.length() : 0);
                return Unification.fail("文件保存失败，请重试");
            }
            
            // 返回相对路径（用于存储到数据库）
            String relativePath = "upload/img/" + fileName;
            logger.info("图片上传成功，保存路径：{}", relativePath);
            return Unification.success(relativePath);
            
        } catch (Exception e) {
            logger.error("图片上传异常：", e);
            return Unification.fail("图片上传失败：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

}

