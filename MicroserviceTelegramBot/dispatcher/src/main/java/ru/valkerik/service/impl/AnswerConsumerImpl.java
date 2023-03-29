package ru.valkerik.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.valkerik.controller.UpdateProcessor;
import ru.valkerik.service.AnswerConsumer;

import static ru.valkerik.model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
@Slf4j
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }


    @Override
    @RabbitListener(queues = ANSWER_MESSAGE_UPDATE)
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
