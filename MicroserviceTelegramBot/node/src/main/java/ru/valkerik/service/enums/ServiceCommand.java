package ru.valkerik.service.enums;

import java.util.Arrays;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;


    ServiceCommand(String cmd) {
        this.value = cmd;
    }

    public static ServiceCommand fromValue(String text) {
        return Arrays.stream(ServiceCommand.values())
                .filter(sc -> sc.value.equals(text))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }

}
