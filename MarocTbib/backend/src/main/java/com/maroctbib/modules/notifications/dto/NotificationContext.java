package com.maroctbib.modules.notifications.dto;

import java.time.ZonedDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationContext {
    String appointmentId;
    String patientName;
    String patientEmail;
    String doctorName;
    String specialty;
    String location;
    ZonedDateTime appointmentDateTime;
    Map<String, Object> extra;
}
