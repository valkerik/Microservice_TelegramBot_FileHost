package ru.valkerik.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.valkerik.entity.AppDocument;
import ru.valkerik.entity.AppPhoto;
import ru.valkerik.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);

}
