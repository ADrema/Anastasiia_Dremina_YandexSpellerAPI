package ru.yandex.speller.api.enums;

public enum ParametersEnum {
    PARAM_TEXT("text"),
    PARAM_OPTIONS("options"),
    PARAM_LANG("lang"),
    PARAM_FORMAT("format");

    public String parameter;

    ParametersEnum(String parameter) {
        this.parameter = parameter;
    }
}
