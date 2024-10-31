package coms309.controller;

import coms309.repository.NotificationSettingsRepository;
import coms309.repository.NotificationRepository;
import coms309.entity.Notification;
import coms309.entity.NotificationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationsController {
    @Autowired
    NotificationRepository notiRepo;
    @Autowired
    NotificationSettingsRepository notiSettingRepo;
}
