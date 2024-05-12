package com.example.service;


import com.example.entity.vo.response.NotificationVO;

import java.util.List;

public interface NotificationService {
    List<NotificationVO> findUserNotification(int uid);

    void deleteUserNotification(int id, int uid);

    void deleteUserAllNotification(int uid);

    void addNotification(int uid, String title, String content, String type, String url);
}
