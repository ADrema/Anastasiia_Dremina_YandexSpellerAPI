package ru.yandex.speller.tests;

import io.restassured.http.Method;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import ru.yandex.speller.api.core.YandexSpellerApi;
import ru.yandex.speller.api.core.YandexSpellerResponse;

import java.util.List;

import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.speller.api.constants.YandexSpellerConstants.*;
import static ru.yandex.speller.api.enums.LanguagesEnum.INVALID_LANG;
import static ru.yandex.speller.api.enums.LanguagesEnum.RU;
import static ru.yandex.speller.api.enums.OptionsEnum.*;

public class YandexSpellerJSONTests {

    @Test(dataProviderClass = HttpMethodsDataProvider.class, dataProvider = "methods provider",
            description = "Check different methods")
    public void sendRequest(Method method, int code) {
        YandexSpellerApi.with()
                .text("Song")
                .httpMethod(method)
                .callApi()
                .then()
                .assertThat()
                .statusCode(code);
    }

    @Test(description = "GET request with incorrect Language parameter")
    public void sendIncorrectLanguageParameter() {
        YandexSpellerApi.with()
                .text("Параллелограмм")
                .language(INVALID_LANG)
                .httpMethod(GET)
                .callApi()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("SpellerService: Invalid parameter 'lang'"));
    }

    @Test(description = "GET request. Ignore digits")
    public void checkDigitsIgnored() {
        YandexSpellerApi.with()
                .text(TEXT_WITH_DIGITS)
                .options(IGNORE_DIGITS.option)
                .httpMethod(GET)
                .callApi()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.equalTo("[]"));
    }

    //    Test will be failed. The function doesn't work
    @Test(description = "POST request. language: ru, word with capital letters")
    public void capitalizationErrorTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text("парАлЛелограмм").language(RU).options("")
                        .callApi()
        );
        assertThat(response, Matchers.hasSize(1));
        assertThat(response.get(0).code, equalTo(3));
        assertThat(response.get(0).word, equalTo("парАлЛелограмм"));
        assertThat(response.get(0).s, equalTo("Параллелограмм"));
    }

    @Test(description = "GET request. Incorrect format parameter")
    public void incorrectFormatParameter() {
        YandexSpellerApi.with()
                .text("Параллелограмм")
                .format("text")
                .httpMethod(GET)
                .callApi()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("SpellerService: Invalid parameter 'format'"));
    }

    //    Test will be failed. The function doesn't work
    @Test(description = "POST request. Text contains repeated word")
    public void checkRepeatWordError() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_REPEATED_WORD)
                        .options(FIND_REPEAT_WORDS.option)
                        .callApi()
        );
        assertThat(response, Matchers.hasSize(1));
        assertThat(response.get(0).code, equalTo(2));
        assertThat(response.get(0).word, equalTo("capital"));
    }

    @Test(description = "POST request. Text with errors")
    public void sendTextWithErrors() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_ERRORS)
                        .language(RU)
                        .callApi()
        );
        assertThat(response.get(0).code, equalTo(1));
        assertThat(response.get(0).word, equalTo("могазин"));
        assertThat(response.get(0).s, contains("магазин"));
        assertThat(response.get(1).code, equalTo(1));
        assertThat(response.get(1).word, equalTo("прадавец"));
        assertThat(response.get(1).s, contains("продавец"));
    }

    @Test(description = "GET request. Ignore URL test")
    public void ignoreURLTest() {
        YandexSpellerApi.with()
                .text(TEXT_WITH_URL)
                .options(IGNORE_URLS.option)
                .httpMethod(GET)
                .callApi()
                .then().specification(YandexSpellerApi.successResponse())
                .body(Matchers.equalTo("[]"));
    }

    // Test will be failed. The function doesn't work
    @Test(description = "POST request. URL should be checked")
    public void checkURLTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_URL)
                        .callApi()
        );
        assertThat(response, Matchers.hasSize(1));
        assertThat(response.get(0).code, equalTo(1));
        assertThat(response.get(0).word, equalTo("spellservice"));
        assertThat(response.get(0).s, equalTo("spell service"));
    }
}
