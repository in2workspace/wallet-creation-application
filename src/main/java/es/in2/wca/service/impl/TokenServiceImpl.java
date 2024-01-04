package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.domain.AuthorisationServerMetadata;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.domain.TokenResponse;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<TokenResponse> getPreAuthorizedToken(String processId, CredentialOffer credentialOffer,
                                                     AuthorisationServerMetadata authorisationServerMetadata) {
        String tokenURL = authorisationServerMetadata.tokenEndpoint();
        // Get Pre-Authorized Token
        return getPreAuthorizedToken(tokenURL, credentialOffer)
                .doOnSuccess(tokenResponse -> log.info("ProcessID: {} - Pre-Authorized Token Response: {}", processId, tokenResponse))
                // Parse Token Response
                .flatMap(this::parseTokenResponse)
                .doOnSuccess(tokenResponse -> log.info("ProcessID: {} - Token Response: {}", processId, tokenResponse))
                .onErrorResume(e -> {
                    log.error("ProcessID: {} - Error while processing Token Response from the Issuer: {}", processId, e.getMessage());
                    return Mono.error(new RuntimeException("Error while processing Token Response from the Issuer"));
                });
    }

    private Mono<String> getPreAuthorizedToken(String tokenURL, CredentialOffer credentialOffer) {
        // Headers
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));
        // Build URL encoded form data request body
        Map<String, String> formDataMap = Map.of("grant_type", PRE_AUTH_CODE_GRANT_TYPE, "pre-authorized_code", credentialOffer.grant().preAuthorizedCodeGrant().preAuthorizedCode());
        String xWwwFormUrlencodedBody = formDataMap.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        // Post request
        return postRequest(tokenURL, headers, xWwwFormUrlencodedBody)
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching Credential Issuer Metadata from the Issuer")));
    }

    private Mono<TokenResponse> parseTokenResponse(String response) {
            try {
            return Mono.just(objectMapper.readValue(response, TokenResponse.class));
        } catch (Exception e) {
            return Mono.error(new FailedDeserializingException("Error while deserializing Credential Issuer Metadata: " + e));
        }
    }

}
