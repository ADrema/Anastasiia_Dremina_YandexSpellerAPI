package ru.yandex.speller.tests;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
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
import static ru.yandex.speller.api.enums.ParametersEnum.*;

public class YandexSpellerJSONTests {

    @Test(description = "POST request. En word without errors")
    public void sendPostRequest() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .queryParam(PARAM_TEXT.parameter, "Song")
                .log().all()
                .post()
                .prettyPeek()
                .then().specification(YandexSpellerApi.successResponse())
                .assertThat()
                .body(Matchers.equalTo("[]"));
    }

    @Test(description = "GET request. En word without errors")
    public void sendGetRequest() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, "Song")
                .accept(ContentType.JSON)
                .get()
                .then().specification(YandexSpellerApi.successResponse())
                .assertThat()
                .body(Matchers.equalTo("[]"));
    }

    @Test(description = "PUT request. Method is not allowed")
    public void sendPutRequest() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, "Song")
                .put()
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "PATCH request. Method is not allowed")
    public void sendPatchRequest() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, "Song")
                .patch()
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "DELETE request. Method is not allowed")
    public void sendDeleteRequest() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, "Song")
                .delete()
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .time(lessThan(5000L));
    }

    @Test(description = "GET request with incorrect Language parameter")
    public void sendIncorrectLanguageParameter() {
        RestAssured
                .given()
                .param(PARAM_TEXT.parameter, "Параллелограмм")
                .param(PARAM_LANG.parameter, INVALID_LANG.language)
                .accept(ContentType.JSON)
                .get(YANDEX_SPELLER_API_URI)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo("SpellerService: Invalid parameter 'lang'"));
    }

    @Test(description = "GET request. Ignore digits")
    public void checkDigitsIgnored() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, TEXT_WITH_DIGITS)
                .param(PARAM_OPTIONS.parameter, IGNORE_DIGITS.option)
                .get()
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.equalTo("[]"));
    }

    //    Test will be failed. The function doesn't work
    @Test(description = "POST request. language: ru, word with capital letters")
    public void capitalizationErrorTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text("парАлЛелограмм").language(RU).options("").callApi()
        );
        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).code, equalTo(3));
        assertThat(response.get(0).word, equalTo("парАлЛелограмм"));
        assertThat(response.get(0).s, equalTo("Параллелограмм"));
    }

    @Test(description = "GET request. Incorrect format parameter")
    public void incorrectFormatParameter() {
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, "Параллелограмм")
                .param(PARAM_FORMAT.parameter, "text")
                .get()
                .prettyPeek()
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
                        .options(FIND_REPEAT_WORDS.option).callApi()
        );
        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).code, equalTo(2));
        assertThat(response.get(0).word, equalTo("capital"));
    }

    @Test(description = "POST request. Text with errors")
    public void sendTextWithErrors() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_ERRORS)
                        .language(RU).callApi()
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
        RestAssured
                .given(YandexSpellerApi.baseRequestConfiguration())
                .param(PARAM_TEXT.parameter, TEXT_WITH_URL)
                .param(PARAM_OPTIONS.parameter, IGNORE_URLS.option)
                .get()
                .prettyPeek()
                .then().specification(YandexSpellerApi.successResponse())
                .body(Matchers.equalTo("[]"));
    }

    // Test will be failed. The function doesn't work
    @Test(description = "POST request. URL should be checked")
    public void checkURLTest() {
        List<YandexSpellerResponse> response = YandexSpellerApi.getYandexSpellerAnswers(
                YandexSpellerApi.with().text(TEXT_WITH_URL).callApi()
        );
        assertThat(response.size(), equalTo(1));
        assertThat(response.get(0).code, equalTo(1));
        assertThat(response.get(0).word, equalTo("spellservice"));
        assertThat(response.get(0).s, equalTo("spell service"));
    }
}
