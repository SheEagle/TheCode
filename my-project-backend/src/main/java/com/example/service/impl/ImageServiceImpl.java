package com.example.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.StoreImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {

    @Resource
    MinioClient client;

    @Resource
    AccountMapper mapper;

    @Resource
    FlowUtils flowUtils;


    SimpleDateFormat format = new SimpleDateFormat("yyyMMdd");

    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        imageName = "/avatar/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();

        try {
            client.putObject(args);
            String avatar = mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            if (mapper.update(null, Wrappers.<Account>update()
                    .eq("id", id).set("avatar", imageName)) > 0) {
                return imageName;
            } else
                return null;
        } catch (Exception e) {
            log.error("图片上传出现问题" + e.getMessage(), e);
            return null;
        }

    }

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(image)
                .build();
        GetObjectResponse response = client.getObject(args);
        IOUtils.copy(response, stream);
    }

    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        // 设置限制上传图片的周期
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 3600))  // 检查上传频率是否超过限制
            return null;  // 如果超过限制，则返回 null

        // 生成唯一的图片名称
        String imageName = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;  // 构造图片保存路径

        // 构建对象上传参数
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")  // 存储桶名称
                .stream(file.getInputStream(), file.getSize(), -1)  // 上传文件的输入流和大小
                .object(imageName)  // 对象名称（图片保存路径）
                .build();

        try {
            // 上传图片到对象存储
            client.putObject(args);
            // 将图片信息保存到数据库
            if (this.save(new StoreImage(id, imageName, date))) {
                return imageName;  // 返回图片保存路径
            } else
                return null;  // 数据库保存失败，返回 null
        } catch (Exception e) {
            log.error("图片上传出现问题" + e.getMessage(), e);  // 记录上传图片异常
            return null;  // 返回 null
        }
    }


    private void deleteOldAvatar(String avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build();
        client.removeObject(remove);
    }
}
