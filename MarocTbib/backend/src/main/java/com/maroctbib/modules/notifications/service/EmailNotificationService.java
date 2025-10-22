package com.maroctbib.modules.notifications.service;

import com.maroctbib.modules.notifications.NotificationChannel;
import com.maroctbib.modules.notifications.NotificationTemplate;
import com.maroctbib.modules.notifications.dto.NotificationContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    public EmailNotificationService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${mail.from:no-reply@maroctbib.ma}")
    private String from;

    @Value("${notify.email.enabled:true}")
    private boolean emailEnabled;

    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void send(NotificationChannel channel, NotificationTemplate template, String recipient, NotificationContext ctx) {
        if (channel != NotificationChannel.EMAIL || !emailEnabled) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subjectFor(template, ctx));
            helper.setText(render(template, ctx), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String render(NotificationTemplate template, NotificationContext ctx) {
        Context context = new Context();
        context.setVariable("ctx", ctx);
        String name = switch (template) {
            case APPOINTMENT_CONFIRMED -> "appointment_confirmed";
            case APPOINTMENT_REMINDER_MINUS1D -> "appointment_reminder_minus1d";
            case APPOINTMENT_CANCELLED -> "appointment_cancelled";
        };
        return templateEngine.process(name, context);
    }

    private String subjectFor(NotificationTemplate template, NotificationContext ctx) {
        return switch (template) {
            case APPOINTMENT_CONFIRMED -> "Confirmation de rendez-vous";
            case APPOINTMENT_REMINDER_MINUS1D -> "Rappel: rendez-vous demain";
            case APPOINTMENT_CANCELLED -> "Annulation de rendez-vous";
        };
    }
}
