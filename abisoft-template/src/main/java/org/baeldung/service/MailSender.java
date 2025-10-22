package org.baeldung.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.baeldung.web.util.MailContent;

@Service
@Component
public class MailSender {
    
    @Autowired
    private MailClient mailClient;
    
    public void sendEmail(String email, String text, String subject, String action, String url, String greeting, String contextPath) {
        try {
            MailContent mailContent = new MailContent(null, subject, null, null);
            String[] bodyMessages = { greeting, text };
            mailContent.buildListOfBodyContent(bodyMessages);
            // mailContent.defaultFooter();
            mailContent.setLogo(contextPath + "/assets/images/logo1.png");
            mailContent.setAction(action);
            mailContent.setUrl(url);
            mailClient.prepareAndSendCustom(email, mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
