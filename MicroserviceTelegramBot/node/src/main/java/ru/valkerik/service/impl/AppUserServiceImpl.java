package ru.valkerik.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.valkerik.dao.AppUserDao;
import ru.valkerik.dto.MailParams;
import ru.valkerik.entity.AppUser;
import ru.valkerik.service.AppUserService;
import ru.valkerik.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

import static ru.valkerik.entity.enums.UserState.BASIC_STATE;
import static ru.valkerik.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Autowired
    public AppUserServiceImpl(AppUserDao appUserDao, CryptoTool cryptoTool) {
        this.appUserDao = appUserDao;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегистрированы!";
        }else if (appUser.getEmail() != null){
            return """
                    Вам на почту уже было отправлено письмо.
                    Перейдите по ссылке в письме для подтверждения регистрации.
                    Если письма нет, проверьте папку спам.""";
        }
        appUser.setUserState(WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Введите, пожалуйста, Ваш email: ";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {

        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
        }
        Optional<AppUser> optionalAppUser = appUserDao.findByEmail(email);
        if (optionalAppUser.isEmpty()){
            appUser.setEmail(email);
            appUser.setUserState(BASIC_STATE);
            appUser = appUserDao.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailServise(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK){
                String msg = String.format("Отправка электронного письма на почту %S не удалась.", email);
                log.error("Отправка электронного письма на почту {} не удалась.", email);
                appUser.setEmail(null);
                appUserDao.save(appUser);
                return msg;
            }
            return """
                    Вам на почту было отправлено письмо.
                    Перейдите по ссылке в письме для подтверждения регистрации.
                    Если письма нет, проверьте папку спам.""";

        }else {
            return """
                    Этот email уже используется.
                    Введите корректный email.
                    Для отмены команды введите /cancel""";
        }
    }

    private ResponseEntity<String> sendRequestToMailServise(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        HttpEntity<MailParams> request = new HttpEntity<MailParams>(mailParams, httpHeaders);
        return restTemplate.exchange(mailServiceUri, HttpMethod.POST, request, String.class);
    }
}
