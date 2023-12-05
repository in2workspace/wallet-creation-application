package es.in2.wca.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
public class Utils {

    Utils() {
        throw new IllegalStateException("Utility class");
    }

    private static final WebClient WEB_CLIENT = WebClient.builder().build();
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_URL_ENCODED_FORM = "application/x-www-form-urlencoded";
    public static final String INVALID_AUTH_HEADER = "Invalid Authorization header";
    public static final String BEARER = "Bearer ";
    public static final String ISSUER_TOKEN_PROPERTY_NAME = "iss";
    public static final String ISSUER_SUB = "sub";
    public static final String JWT_VC = "jwt_vc";
    public static final String JWT_VP = "jwt_vp";
    public static final String GET_SELECTABLE_VCS = "/api/v1/credentials/types";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PRE_AUTH_CODE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:pre-authorized_code";
    public static final String SERVICE_MATRIX = "service-matrix.properties";

    public static final String GLOBAL_ENDPOINTS_API = "/api/v1/*";
    public static final String ALLOWED_METHODS = "*";
    public static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    public static Mono<String> postRequest(String url, List<Map.Entry<String, String>> headers, String body) {
        return WEB_CLIENT.post()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK && status != HttpStatus.CREATED, clientResponse ->
                        Mono.error(new RuntimeException("Error during post request:" + clientResponse.statusCode())))
                .bodyToMono(String.class);
    }

    public static Mono<String> getRequest(String url, List<Map.Entry<String, String>> headers) {
        return WEB_CLIENT.get()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .retrieve()
                .bodyToMono(String.class);
    }

    public static Mono<String> getCleanBearerToken(String authorizationHeader) {
        return Mono.just(authorizationHeader)
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.replace(BEARER, "").trim())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER)));
    }

}
