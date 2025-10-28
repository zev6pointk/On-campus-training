package com.teacher.assistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * SpringBoot主启动类
 *
 * @author teacher
 * @since 2025-10-28
 */
@SpringBootApplication
@MapperScan("com.teacher.assistant.mapper")
public class AssistantApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
        System.out.println("=================================");
        System.out.println("师生助手系统启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("=================================");
    }

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("================ Password Generator ================");
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("password: " + encoder.encode("password"));
        System.out.println("=================================================");
    }
}
