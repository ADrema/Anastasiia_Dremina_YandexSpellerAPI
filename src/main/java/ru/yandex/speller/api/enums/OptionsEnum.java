package ru.yandex.speller.api.enums;

public enum OptionsEnum {
    IGNORE_DIGITS("2"),
    IGNORE_URLS("4"),
    FIND_REPEAT_WORDS("8"),
    IGNORE_CAPITALIZATION("512");

    public String option;

    OptionsEnum(String option) {
        this.option = option;
    }
}
