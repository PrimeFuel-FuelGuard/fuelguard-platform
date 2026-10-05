package com.primefuel.fuelguard.platform.notification.application.commandservices;

import com.primefuel.fuelguard.platform.notification.domain.model.aggregates.Notification;
import com.primefuel.fuelguard.platform.notification.domain.model.commands.MarkNotificationAsReadCommand;
import com.primefuel.fuelguard.platform.shared.application.result.ApplicationError;
import com.primefuel.fuelguard.platform.shared.application.result.Result;

public interface NotificationCommandService {
    Result<Notification, ApplicationError> handle(MarkNotificationAsReadCommand command);
}
