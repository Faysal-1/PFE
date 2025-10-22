package com.maroctbib.modules.notifications.service;

import com.maroctbib.modules.notifications.NotificationChannel;
import com.maroctbib.modules.notifications.NotificationTemplate;
import com.maroctbib.modules.notifications.dto.NotificationContext;

public interface NotificationService {
    void send(NotificationChannel channel, NotificationTemplate template, String recipient, NotificationContext ctx);
}
