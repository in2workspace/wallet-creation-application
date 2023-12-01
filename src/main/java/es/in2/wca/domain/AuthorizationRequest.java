package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(input);
        Map<String, List<String>> queryParams = builder.build().getQueryParams();
        String scope = queryParams.getOrDefault("scope", List.of("")).get(0);
        String responseType = queryParams.getOrDefault("response_type", List.of("")).get(0);
        String responseMode = queryParams.getOrDefault("response_mode", List.of("")).get(0);
        String clientId = queryParams.getOrDefault("client_id", List.of("")).get(0);
        String redirectUri = queryParams.getOrDefault("redirect_uri", List.of("")).get(0);
        String state = queryParams.getOrDefault("state", List.of("")).get(0);
        String nonce = queryParams.getOrDefault("nonce", List.of("")).get(0);
        // scope = [data1, data2, dataN]
        String scopeChained = scope.replace("[", "").replace("]", "").trim();
        List<String> scopeList = Arrays.asList(scopeChained.split(","));
        return AuthorizationRequest.builder()
                .scope(scopeList)
                .responseType(responseType)
                .responseMode(responseMode)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .state(state)
                .nonce(nonce)
                .build();
    }

}
