package ru.yandex.speller.tests;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import ru.yandex.speller.api.core.YandexSpellerApi;
import ru.yandex.speller.api.core.YandexSpellerResponse;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.yandex.speller.api.constants.YandexSpellerConstants.*;
import static ru.yandex.speller.api.enums.LanguagesEnum.INVALID_LANG;
import static ru.yandex.speller.api.enums.LanguagesEnum.RU;
import static ru.yandex.speller.api.enums.OptionsEnum.*;

public class YandexSpellerJSONTests {

    @Test(description = "POST request. En word without errors")
    public void sendPostRequest() {
        YandexSpellerApi.with()
                .text("Song")
                .callApi(HttpPost.METHOD_NAME)
                .then().specification(YandexSpellerApi.successResponse())
                .assertThat()
                .body(Matchers.equalTo("[]"));
    }

    @Test(description = "GET request. En word without errors")
    public void sendGetRequest() {
        YandexSpellerApi.with()
                .text("Song")
                .callApi(HttpGet.METHOD_NAME)
                .then().specification(YandexSpellerApi.successResponse())
                .assertThat()
                .body(Matchers.equalTo("[]"));
    }

    @Test(description = "PUT request. Method is not allowed")
    public void sendPutRequest() {
        YandexSpellerApi.with()
                .text("Song")
                .callApi(HttpPut.METHOD_NAME)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "PATCH request. Method is not allowed")
    public void sendPatchRequest() {
        YandexSpellerApi.with()
                .text("Song")
                .callApi(HttpPatch.METHOD_NAME)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "DELETE request. Method is not allowed")
    public void sendDeleteRequest() {
        YandexSpellerApi.with()
                .text("Song")
                .callApi(HttpDelete.METHOD_NAME)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "GET request with incorrect Language parameter")
    public void sendIncorrectLanguageParameter() {
        YandexSpellerApi.with()
                .text("Параллелограмм")
                .language(INVALID_LANG)
                .callApi(HttpGet.METHOD_NAME)
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
                .callApi(HttpGet.METHOD_NAME)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.equalTo("[]"));
    }

    //    Test will be failed. The function doesn't work
    @Test(description = "POST request. language: ru, word with capital letters")
    public void capitalizationErrorTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text("парАлЛелограмм").language(RU).options("").callApi(HttpPost.METHOD_NAME)
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
                .callApi(HttpGet.METHOD_NAME)
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
                        .options(FIND_REPEAT_WORDS.option).callApi(HttpPost.METHOD_NAME)
        );
        assertThat(response, Matchers.hasSize(1));
        assertThat(response.get(0).code, equalTo(2));
        assertThat(response.get(0).word, equalTo("capital"));
    }

    @Test(description = "POST request. Text with errors")
    public void sendTextWithErrors() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_ERRORS)
                        .language(RU).callApi(HttpGet.METHOD_NAME)
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
                .callApi(HttpGet.METHOD_NAME)
                .then().specification(YandexSpellerApi.successResponse())
                .body(Matchers.equalTo("[]"));
    }

    // Test will be failed. The function doesn't work
    @Test(description = "POST request. URL should be checked")
    public void checkURLTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_URL).callApi(HttpPost.METHOD_NAME)
        );
        assertThat(response, Matchers.hasSize(1));
        assertThat(response.get(0).code, equalTo(1));
        assertThat(response.get(0).word, equalTo("spellservice"));
        assertThat(response.get(0).s, equalTo("spell service"));
    }
}
