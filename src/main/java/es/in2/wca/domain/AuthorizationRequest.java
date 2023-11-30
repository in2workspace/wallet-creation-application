package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Builder
public record AuthorizationRequest(
        @JsonProperty("scope") List<String> scope,
        @JsonProperty("response_type") String responseType, // always "vp_token"
        @JsonProperty("response_mode") String responseMode, // always "form_post"
        @JsonProperty("client_id") String clientId,
        @JsonProperty("state") String state,
        @JsonProperty("nonce") String nonce,
        @JsonProperty("redirect_uri") String redirectUri

//      @JsonProperty("issuer_state") String issuerState,
//      @JsonProperty("authorization_details") String authorizationDetails,
//      @JsonProperty("code_challenge") String codeChallenge,
//      @JsonProperty("code_challenge_method") String codeChallengeMethod,
//      @JsonProperty("client_metadata") String clientMetadata

) {

    public static AuthorizationRequest fromString(String input) {
        // Delete "openid://?" prefix
        String cleanedInput = input.replace("openid://?", "");
        Map<String, List<String>> queryParams = UriComponentsBuilder.fromUriString(cleanedInput).build().getQueryParams();
        List<String> scope = queryParams.getOrDefault("scope", List.of(""));
        String responseType = queryParams.getOrDefault("response_type", List.of("")).get(0);
        String responseMode = queryParams.getOrDefault("response_mode", List.of("")).get(0);
        String clientId = queryParams.getOrDefault("client_id", List.of("")).get(0);
        String redirectUri = queryParams.getOrDefault("redirect_uri", List.of("")).get(0);
        String state = queryParams.getOrDefault("state", List.of("")).get(0);
        String nonce = queryParams.getOrDefault("nonce", List.of("")).get(0);

        return AuthorizationRequest.builder()
                .scope(scope)
                .responseType(responseType)
                .responseMode(responseMode)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .state(state)
                .nonce(nonce)
                .build();

//        return OpenIdConfig.builder()
//                .scope(scope)
//                .responseType(responseType)
//                .responseMode(responseMode)
//                .clientId(clientId)
//                .redirectUri(redirectUri)
//                .state(state)
//                .nonce(nonce).build();
//
//
//
//        // Divide parameters for "&" as delimiter
//        String[] params = cleanedInput.split("&");
//        // Create a map with the parameters
//        Map<String, String> paramMap = new HashMap<>(Map.of());
//        Arrays.stream(params).parallel().forEach(param -> {
//            String[] keyValue = param.split("=");
//            if (keyValue.length == 2) {
//                paramMap.put(keyValue[0], keyValue[1]);
//            }
//        });
//        List<String> scopeList = Arrays.asList(paramMap.getOrDefault("scope", "").split(","));
//        return new AuthorizationRequest(
//                scopeList,
//                paramMap.get("response_type"),
//                paramMap.get("response_mode"),
//                paramMap.get("client_id"),
//                paramMap.get("state"),
//                paramMap.get("nonce"),
//                paramMap.get("redirect_uri")
//        );
    }

}
