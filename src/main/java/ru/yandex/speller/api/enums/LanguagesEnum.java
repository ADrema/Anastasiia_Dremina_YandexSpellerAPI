package ru.yandex.speller.api.enums;

public enum LanguagesEnum {
    RU("ru"),
    EN("en"),
    UK("uk"),
    INVALID_LANG("lu");

    public String language;

    LanguagesEnum(String lang) {
        this.language = lang;
    }
}
