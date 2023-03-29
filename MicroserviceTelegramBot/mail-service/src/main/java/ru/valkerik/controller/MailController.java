package ru.valkerik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.valkerik.dto.MailParams;
import ru.valkerik.service.MailSenderService;

@RequestMapping("/mail")
@RestController
public class MailController {

    private final MailSenderService mailSenderService;

    @Autowired
    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams){
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
