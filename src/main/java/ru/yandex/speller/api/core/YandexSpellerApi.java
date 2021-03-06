package ru.yandex.speller.api.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import ru.yandex.speller.api.constants.YandexSpellerConstants;
import ru.yandex.speller.api.enums.LanguagesEnum;

import java.util.HashMap;
import java.util.List;

import static io.restassured.http.Method.POST;
import static org.hamcrest.Matchers.lessThan;
import static ru.yandex.speller.api.enums.ParametersEnum.*;

public class YandexSpellerApi {
    //TODO: 1. do not use particular implementation without necessity.
    //TODO: 2. No needs to add types into initialization part.
    private HashMap<String, String> params = new HashMap<String, String>();
    public Method method = POST;

    private YandexSpellerApi() {
    }

    public static ApiBuilder with() {
        YandexSpellerApi api = new YandexSpellerApi();
        return new ApiBuilder(api);
    }

    //get ready Speller answers list form api response
    public static List<YandexSpellerResponse> getYandexSpellerAnswers(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<YandexSpellerResponse>>(){
        }.getType());
    }

    //set base request and response specifications to use in tests
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

        public ApiBuilder httpMethod(Method requestMethod) {
            spellerApi.method = requestMethod;
            return this;
        }

        public Response callApi() {
            return RestAssured.with()
                    .accept(ContentType.JSON)
                    .headers("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .params(spellerApi.params)
                    .log()
                    .all()
                    .request(spellerApi.method, YandexSpellerConstants.YANDEX_SPELLER_API_URI)
                    .prettyPeek();
        }
    }
}
