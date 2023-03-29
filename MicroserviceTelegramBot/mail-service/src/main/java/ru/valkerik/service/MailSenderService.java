package ru.valkerik.service;

import ru.valkerik.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);
}
