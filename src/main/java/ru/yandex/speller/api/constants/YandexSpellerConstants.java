package ru.yandex.speller.api.constants;

public class YandexSpellerConstants {
    //Constants used during the tests
    public static final String YANDEX_SPELLER_API_URI = "https://speller.yandex.net/services/spellservice.json/checkText";
    public static final String TEXT_WITH_REPEATED_WORD = "London is the capital capital of Great Britain";
    public static final String TEXT_WITH_URL = "Test should ignore the following URL https://speller.yandex.net/services/spellservice.json";
    public static final String TEXT_WITH_DIGITS = "London is the capi123tal of Great Britain";
    public static final String TEXT_WITH_ERRORS = "Бабушка слезно просила купить ей мясца " +
            "Я захожу в могазин там стоит прадавец " +
            "Эй продавец ну-ка взвесь 300 грамм холодца " +
            "Очень уж люб моей бабушке твой холодец ";
}
