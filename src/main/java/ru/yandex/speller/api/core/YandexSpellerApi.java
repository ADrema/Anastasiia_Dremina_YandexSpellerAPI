package ru.yandex.speller.api.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import ru.yandex.speller.api.constants.YandexSpellerConstants;
import ru.yandex.speller.api.enums.LanguagesEnum;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.lessThan;
import static ru.yandex.speller.api.enums.ParametersEnum.*;

public class YandexSpellerApi {

    private HashMap<String, String> params = new HashMap<String, String>();

    private YandexSpellerApi() {
    }

    public static ApiBuilder with() {
        YandexSpellerApi api = new YandexSpellerApi();
        return new ApiBuilder(api);
    }

    //get ready Speller answers list form api response
    public static List<YandexSpellerResponse> getYandexSpellerAnswers(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<YandexSpellerResponse>>() {
        }.getType());
    }

    //set base request and response specifications tu use in tests
    public static ResponseSpecification successResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(10000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static RequestSpecification baseRequestConfiguration() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .setBaseUri(YandexSpellerConstants.YANDEX_SPELLER_API_URI)
                .build();
    }

    public static class ApiBuilder {
        YandexSpellerApi spellerApi;

        private ApiBuilder(YandexSpellerApi gcApi) {
            spellerApi = gcApi;
        }

        public ApiBuilder text(String text) {
            spellerApi.params.put(PARAM_TEXT.parameter, text);
            return this;
        }

        public ApiBuilder options(String options) {
            spellerApi.params.put(PARAM_OPTIONS.parameter, options);
            return this;
        }

        public ApiBuilder language(LanguagesEnum language) {
            spellerApi.params.put(PARAM_LANG.parameter, language.language);
            return this;
        }

        public ApiBuilder format(String format) {
            spellerApi.params.put(PARAM_FORMAT.parameter, format);
            return this;
        }

        public Response callApi(String method) {
            RequestSpecification request = RestAssured.with()
                    .accept(ContentType.JSON)
                    .headers("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .log().all();
            Response response;
            switch (method) {
                case HttpGet.METHOD_NAME:
                    response = request.params(spellerApi.params)
                            .get(YandexSpellerConstants.YANDEX_SPELLER_API_URI);
                    break;
                case  HttpPut.METHOD_NAME:
                    response = request.params(spellerApi.params)
                            .put(YandexSpellerConstants.YANDEX_SPELLER_API_URI);
                    break;
                case HttpPatch.METHOD_NAME:
                    response = request.params(spellerApi.params)
                            .patch(YandexSpellerConstants.YANDEX_SPELLER_API_URI);
                    break;
                case HttpDelete.METHOD_NAME:
                    response = request.params(spellerApi.params)
                            .delete(YandexSpellerConstants.YANDEX_SPELLER_API_URI);
                    break;
                default:
                    response = request.queryParams(spellerApi.params)
                            .post(YandexSpellerConstants.YANDEX_SPELLER_API_URI);

                    break;
            }
            return response.prettyPeek();
        }
    }
}
