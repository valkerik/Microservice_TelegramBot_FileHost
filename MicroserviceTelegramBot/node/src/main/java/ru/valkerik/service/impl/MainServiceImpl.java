package ru.valkerik.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.valkerik.dao.AppUserDao;
import ru.valkerik.dao.RawDataDAO;
import ru.valkerik.entity.AppDocument;
import ru.valkerik.entity.AppPhoto;
import ru.valkerik.entity.AppUser;
import ru.valkerik.entity.RawData;
import ru.valkerik.entity.enums.UserState;
import ru.valkerik.exceptions.UploadFileException;
import ru.valkerik.service.AppUserService;
import ru.valkerik.service.FileService;
import ru.valkerik.service.MainService;
import ru.valkerik.service.ProducerService;
import ru.valkerik.service.enums.LinkType;
import ru.valkerik.service.enums.ServiceCommand;

import java.util.Optional;

import static ru.valkerik.entity.enums.UserState.BASIC_STATE;
import static ru.valkerik.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.valkerik.service.enums.ServiceCommand.*;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;
    private final AppUserService appUserService;

    @Autowired
    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDao appUserDao, FileService fileService, AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getUserState();
        String text = update.getMessage().getText();
        String output = "";

        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown UserState {}", userState);
            output = "Неизвестная ошибка. Введите /cancel и попробуйте снова";
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocumentMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            String answer = "Документ успешно загружен! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error("Error {}", e.getMessage());
            String error = "К сожалению загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);

        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            String answer = "Фото успешно загружено! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error("Error {}", e.getMessage());
            String error = "К сожалению загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getUserState();
        if (!appUser.getIsActive()) {
            String error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента!";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            String error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            //TODO добавить регистрацию
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Бот приветствует тебя! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel - отмена выполнения текущей команды; \n"
                + "/registration - регистрация пользователя.";

    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();

        Optional<AppUser> persistentAppUser = appUserDao.findByTelegramUserId(telegramUser.getId());
        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = new RawData(update);
        rawDataDAO.save(rawData);
    }


}
