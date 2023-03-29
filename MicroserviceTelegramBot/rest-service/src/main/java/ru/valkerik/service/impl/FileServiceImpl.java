package ru.valkerik.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.valkerik.dao.AppDocumentDAO;
import ru.valkerik.dao.AppPhotoDAO;
import ru.valkerik.entity.AppDocument;
import ru.valkerik.entity.AppPhoto;
import ru.valkerik.service.FileService;
import ru.valkerik.utils.CryptoTool;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    @Autowired
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String docId) {
       Long id = cryptoTool.idOf(docId);
       if (id == null){
           return null;
       }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        Long id = cryptoTool.idOf(photoId);
        if (id == null){
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }
}
