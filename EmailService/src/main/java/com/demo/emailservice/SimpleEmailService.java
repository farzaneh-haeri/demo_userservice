package com.demo.emailservice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SimpleEmailService implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendMail(EmailDto email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(email.getEmailFrom());
            message.setTo(email.getEmailTo());
            message.setSubject(email.getSubject());
            message.setText(email.getText());

            javaMailSender.send(message);

            log.info("Welcome email was sent to email address {}", email.getEmailTo());
        } catch (final Exception exception) {
            log.error("Welcome email could not be sent to email address {}", email.getEmailTo());
        }
    }

}
