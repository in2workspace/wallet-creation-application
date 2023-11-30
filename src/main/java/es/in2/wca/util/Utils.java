package es.in2.wca.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Utils {

    private static final WebClient webClient = WebClient.builder().build();

    Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_URL_ENCODED_FORM = "application/x-www-form-urlencoded";
    public static final String INVALID_AUTH_HEADER = "Invalid Authorization header";
    public static final String BEARER = "Bearer ";
    public static final String ISSUER_TOKEN_PROPERTY_NAME = "iss";
    public static final String ISSUER_SUB = "sub";
    public static final String JWT_VC = "jwt_vc";
    public static final String JWT_VP = "jwt_vp";
    public static final String GET_SELECTABLE_VCS = "/api/credentials/types";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PRE_AUTH_CODE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:pre-authorized_code";
    public static final String SERVICE_MATRIX = "service-matrix.properties";
    public static final String VC_LOGIN_REQUEST_URI = "/api/siop";
    public static final String GET_CREDENTIAL_ISSUER_METADATA = "/api/credentials/issuer-metadata";

    public static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    public static Mono<String> postRequest(String url, List<Map.Entry<String, String>> headers, String body) {
        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK && status != HttpStatus.CREATED, clientResponse ->
                        Mono.error(new RuntimeException("Error during post request:" + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .doOnNext(response -> logCRUD(url, headers, body, response, "POST"));
    }

    public static Mono<String> getRequest(String url, List<Map.Entry<String, String>> headers) {
        return webClient.get()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue())))
                .retrieve()
                .bodyToMono(String.class);
    }

    public static Mono<String> buildUrlEncodedFormDataRequestBody(Map<String, String> formDataMap) {
        return Mono.fromCallable(() ->
                formDataMap.entrySet().stream()
                        .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"))
        );
    }

    private static void logCRUD(String url, List<Map.Entry<String, String>> headers, String requestBody, String responseBody, String method) {
        log.debug("********************************************************************************");
        log.debug(">>> METHOD: {}", method);
        log.debug(">>> URI: {}", url);
        log.debug(">>> HEADERS: {}", headers);
        log.debug(">>> BODY: {}", requestBody);
        log.debug("<<< BODY: {}", responseBody);
        log.debug("********************************************************************************");
    }

    public static Mono<String> getUserIdFromToken(String authorizationHeader) {
        return Mono.just(authorizationHeader)
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(7))
                .flatMap(token -> {
                    try {
                        SignedJWT parsedVcJwt = SignedJWT.parse(token);
                        JsonNode jsonObject = new ObjectMapper().readTree(parsedVcJwt.getPayload().toString());
                        return Mono.just(jsonObject.get("sub").asText());
                    } catch (ParseException | JsonProcessingException e) {
                        return Mono.error(e);
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER)));
    }

//    public static Mono<OpenIdConfig> parseOpenIdConfig(String url) {
//        return Mono.fromCallable(() -> {
//            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
//            Map<String, List<String>> queryParams = builder.build().getQueryParams();
//
//            String scope = queryParams.getOrDefault("scope", List.of("")).get(0);
//            String responseType = queryParams.getOrDefault("response_type", List.of("")).get(0);
//            String responseMode = queryParams.getOrDefault("response_mode", List.of("")).get(0);
//            String clientId = queryParams.getOrDefault("client_id", List.of("")).get(0);
//            String redirectUri = queryParams.getOrDefault("redirect_uri", List.of("")).get(0);
//            String state = queryParams.getOrDefault("state", List.of("")).get(0);
//            String nonce = queryParams.getOrDefault("nonce", List.of("")).get(0);
//
//            return OpenIdConfig.builder()
//                    .scope(scope)
//                    .responseType(responseType)
//                    .responseMode(responseMode)
//                    .clientId(clientId)
//                    .redirectUri(redirectUri)
//                    .state(state)
//                    .nonce(nonce).build();
//        });
//    }

    public static Mono<String> getCleanBearerToken(String authorizationHeader) {
        return Mono.just(authorizationHeader)
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.replace(BEARER, "").trim())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(INVALID_AUTH_HEADER)));
    }

}
