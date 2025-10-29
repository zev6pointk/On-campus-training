package com.teacher.assistant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件控制器
 *
 * @author teacher
 * @since 2025-10-29
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * 获取头像文件
     */
    @GetMapping("/avatar/{filename:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            String uploadDir = "/tmp/uploads/avatars";
            Path filePath = Paths.get(uploadDir).resolve(filename);

            File file = filePath.toFile();
            if (!file.exists() || !file.isFile()) {
                log.warn("头像文件不存在: {}", filename);
                // 返回默认头像或404
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(filePath);

            // 根据文件扩展名设置Content-Type
            String contentType = null;
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".png")) {
                contentType = MediaType.IMAGE_PNG_VALUE;
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = MediaType.IMAGE_JPEG_VALUE;
            } else {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            log.error("获取头像文件失败: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
}
