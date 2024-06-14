package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    ImageService service;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if (file.getSize() > 1024 * 100)
            return RestBean.failure(400, "The size of avatar cannot exceed 100KB");
        log.info("Uploading image...");
        String url = service.uploadAvatar(file, id);
        if (url != null) {
            log.info("Avatar uploaded successfully. Size: " + file.getSize());
            return RestBean.success(url);
        } else {
            return RestBean.failure(400, "Failed to upload avatar. Please contact the administrator.");
        }
    }

    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file") MultipartFile file,
                                        @RequestAttribute(Const.ATTR_USER_ID) int id,
                                        HttpServletResponse response) throws IOException {
        if (file.getSize() > 1024 * 1024 * 5)
            return RestBean.failure(400, "The size of image cannot exceed 5MB");
        log.info("Uploading image...");
        String url = service.uploadImage(file, id);
        if (url != null) {
            log.info("Image uploaded successfully. Size: " + file.getSize());
            return RestBean.success(url);
        } else {
            response.setStatus(400);
            return RestBean.failure(400, "Failed to upload image. Please contact the administrator.");
        }

    }

}
