package es.in2.wca.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.domain.CredentialEbsiFormat;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.exception.ParseErrorException;
import es.in2.wca.service.CredentialOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialOfferServiceImpl implements CredentialOfferService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<CredentialOffer> getCredentialOfferFromCredentialOfferUri(String processId, String credentialOfferUri) {
        return parseCredentialOfferUri(credentialOfferUri)
                .doOnSuccess(credentialOfferUriValue -> log.info("ProcessId: {}, Credential Offer Uri parsed successfully: {}", processId, credentialOfferUriValue))
                .doOnError(e -> log.error("ProcessId: {}, Error while parsing Credential Offer Uri: {}", processId, e.getMessage()))
                .flatMap(this::getCredentialOffer)
                .doOnSuccess(credentialOffer -> log.info("ProcessId: {}, Credential Offer fetched successfully: {}", processId, credentialOffer))
                .doOnError(e -> log.error("ProcessId: {}, Error while fetching Credential Offer: {}", processId, e.getMessage()))
                .flatMap(this::parseCredentialOfferResponse)
                .doOnSuccess(preAuthorizedCredentialOffer -> log.info("ProcessId: {}, Credential Offer parsed successfully: {}", processId, preAuthorizedCredentialOffer))
                .doOnError(e -> log.error("ProcessId: {}, Error while parsing Credential Offer: {}", processId, e.getMessage()));
    }

    private Mono<String> parseCredentialOfferUri(String credentialOfferUri) {
        return Mono.fromCallable(() -> {
                    String[] splitCredentialOfferUri = credentialOfferUri.split("=");
                    String credentialOfferUriValue = splitCredentialOfferUri[1];
                    return URLDecoder.decode(credentialOfferUriValue, StandardCharsets.UTF_8);
                })
                .onErrorResume(e -> Mono.error(new ParseErrorException("Error while parsing credentialOfferUri")));
    }

    private Mono<String> getCredentialOffer(String credentialOfferUri) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        return getRequest(credentialOfferUri, headers)
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching credentialOffer from the issuer")));
    }

    private Mono<CredentialOffer> parseCredentialOfferResponse(String response) {
        try {
            // parse credential offer response to get credential attribute format
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode credentialsNode = jsonNode.path("credentials");
            JsonNode firstElement = credentialsNode.get(0);
            List<?> credentialsList;
            if (firstElement.isTextual()) {
                credentialsList = objectMapper.readValue(credentialsNode.toString(),
                        new TypeReference<List<String>>() {
                        });
            } else {
                credentialsList = objectMapper.readValue(credentialsNode.toString(),
                        new TypeReference<List<CredentialEbsiFormat>>() {
                        });
            }
            return Mono.just(CredentialOffer.builder()
                    .credentialIssuer(jsonNode.path("credential_issuer").asText())
                    .credentials(credentialsList)
                    .grant(objectMapper.readValue(jsonNode.path("grants").toString(), CredentialOffer.Grant.class))
                    .build());
        } catch (Exception e) {
            return Mono.error(new FailedDeserializingException("Error while deserializing CredentialOfferForPreAuthorizedCodeFlow: " + e));
        }
    }

}
