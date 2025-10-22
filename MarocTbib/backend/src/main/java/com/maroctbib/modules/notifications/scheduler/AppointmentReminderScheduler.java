package com.maroctbib.modules.notifications.scheduler;

import com.maroctbib.modules.notifications.NotificationChannel;
import com.maroctbib.modules.notifications.NotificationTemplate;
import com.maroctbib.modules.notifications.dto.NotificationContext;
import com.maroctbib.modules.notifications.service.NotificationService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final NotificationService notificationService;

    // TODO: inject appointment repository/service to find tomorrow appointments

    @Scheduled(cron = "0 0 9 * * *", zone = "Africa/Casablanca")
    public void sendRemindersForTomorrow() {
        // Placeholder: integrate with appointment service
        // Example usage (remove once wired to real data):
        // NotificationContext ctx = NotificationContext.builder()
        //     .appointmentId("demo")
        //     .patientName("Patient Demo")
        //     .patientEmail("patient@example.com")
        //     .doctorName("Dr Demo")
        //     .specialty("General")
        //     .location("Casablanca")
        //     .appointmentDateTime(ZonedDateTime.now(ZoneId.of("Africa/Casablanca")).plusDays(1))
        //     .build();
        // notificationService.send(NotificationChannel.EMAIL, NotificationTemplate.APPOINTMENT_REMINDER_MINUS1D, ctx.getPatientEmail(), ctx);
    }
}
