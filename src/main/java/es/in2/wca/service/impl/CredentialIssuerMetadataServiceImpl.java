package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.configuration.properties.AuthServerProperties;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.service.CredentialIssuerMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialIssuerMetadataServiceImpl implements CredentialIssuerMetadataService {

    private final ObjectMapper objectMapper;
    private final AuthServerProperties authServerProperties;

    @Override
    public Mono<CredentialIssuerMetadata> getCredentialIssuerMetadataFromCredentialOffer(String processId, CredentialOffer credentialOffer) {
        String credentialIssuerURL = credentialOffer.credentialIssuer() + "/.well-known/openid-credential-issuer";
        // get Credential Issuer Metadata
        return getCredentialIssuerMetadata(credentialIssuerURL)
                .doOnSuccess(response -> log.info("ProcessID: {} - Credential Issuer Metadata Response: {}", processId, response))
                .flatMap(this::parseCredentialIssuerMetadataResponse)
                .doOnNext(credentialIssuerMetadata -> log.info("ProcessID: {} - CredentialIssuerMetadata: {}", processId, credentialIssuerMetadata))
                .onErrorResume(e -> {
                    log.error("ProcessID: {} - Error while processing Credential Issuer Metadata from the Issuer: {}", processId, e.getMessage());
                    return Mono.error(new RuntimeException("Error while processing Credential Issuer Metadata from the Issuer"));
                });
    }

    private Mono<String> getCredentialIssuerMetadata(String credentialIssuerURL) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        return getRequest(credentialIssuerURL, headers)
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching Credential Issuer Metadata from the Issuer")));
    }

    /**
     * This method is marked as deprecated and will be replaced in the future.
     * The current implementation manually sets a specific field to maintain
     * backward compatibility with our wallet. Refactoring is planned to improve
     * this logic.
     *
     * @param response The response String to be parsed.
     * @return An instance of Mono<CredentialIssuerMetadata>.
     * @deprecated (since = "1.5", forRemoval = true) This implementation is temporary and should be replaced in future versions.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    private Mono<CredentialIssuerMetadata> parseCredentialIssuerMetadataResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.has("credential_token")) {
                CredentialIssuerMetadata credentialIssuerMetadataOriginal = objectMapper.treeToValue(rootNode, CredentialIssuerMetadata.class);
                CredentialIssuerMetadata credentialIssuerMetadataWithCredentialEndpointHardcoded = CredentialIssuerMetadata.builder()
                        .credentialIssuer(credentialIssuerMetadataOriginal.credentialIssuer())
                        .credentialEndpoint(credentialIssuerMetadataOriginal.credentialEndpoint())
                        .credentialsSupported(credentialIssuerMetadataOriginal.credentialsSupported())
                        .deferredCredentialEndpoint(credentialIssuerMetadataOriginal.deferredCredentialEndpoint())
                        .authorizationServer(authServerProperties.domain())
                        .build();
                return Mono.just(credentialIssuerMetadataWithCredentialEndpointHardcoded);
            }
            else {
                // deserialize Credential Issuer Metadata
                return Mono.just(objectMapper.readValue(response, CredentialIssuerMetadata.class));
            }

        } catch (Exception e) {
            return Mono.error(new FailedDeserializingException("Error while deserializing Credential Issuer Metadata: " + e));
        }
    }

}
