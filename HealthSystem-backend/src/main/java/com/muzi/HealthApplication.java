package com.muzi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
@MapperScan("com.muzi.*.mapper")
public class HealthApplication implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("==========================================");
        logger.info("健康管理系统启动成功！");
        logger.info("==========================================");
    }
}

