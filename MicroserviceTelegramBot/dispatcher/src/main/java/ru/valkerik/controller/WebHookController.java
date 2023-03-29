package ru.valkerik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {

    private final UpdateProcessor updateProcessor;

    @Autowired
    public WebHookController(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/callback/update")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update){
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
