package ru.valkerik.service;

import ru.valkerik.entity.AppDocument;
import ru.valkerik.entity.AppPhoto;

public interface FileService {

    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}
