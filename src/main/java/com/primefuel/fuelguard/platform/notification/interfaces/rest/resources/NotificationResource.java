package com.primefuel.fuelguard.platform.notification.interfaces.rest.resources;

import com.primefuel.fuelguard.platform.notification.domain.model.valueobjects.NotificationType;

public record NotificationResource(Long id, Long userId, NotificationType type,
                                   String title, String message, boolean read, Long referenceId,
                                   String createdAt) {
}
