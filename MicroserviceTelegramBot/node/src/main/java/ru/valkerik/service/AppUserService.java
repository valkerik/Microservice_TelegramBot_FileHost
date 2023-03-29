package ru.valkerik.service;

import ru.valkerik.entity.AppUser;

public interface AppUserService {

    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser, String email);
}
