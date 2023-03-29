package ru.valkerik.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ru.valkerik.dto.MailParams;
import ru.valkerik.service.MailSenderService;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${service.activation.uri}")
    private String activationServiceUri;


    public MailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(MailParams mailParams) {
        String subject = "Активация учетной записи.";
        String messageBody = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailTo);
        mailMessage.setFrom(emailFrom);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        String messageBody = String.format("Активация учетной записи Telegram-Бота.\n" +
                "Для завершения регистрации перейдите по ссылке: \n%s", activationServiceUri);
        return messageBody.replace("{id}", id);
    }
}
