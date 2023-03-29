package ru.valkerik.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.valkerik.dao.AppUserDao;
import ru.valkerik.entity.AppUser;
import ru.valkerik.service.UserActivationService;
import ru.valkerik.utils.CryptoTool;

import java.util.Optional;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;

    @Autowired
    public UserActivationServiceImpl(AppUserDao appUserDao, CryptoTool cryptoTool) {
        this.appUserDao = appUserDao;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> optionalAppUser = appUserDao.findById(userId);
        if (optionalAppUser.isPresent()){
            AppUser user = optionalAppUser.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
