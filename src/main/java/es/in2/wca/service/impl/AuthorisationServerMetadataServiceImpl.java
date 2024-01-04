package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.configuration.properties.AuthServerProperties;
import es.in2.wca.domain.AuthorisationServerMetadata;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.service.AuthorisationServerMetadataService;
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
public class AuthorisationServerMetadataServiceImpl implements AuthorisationServerMetadataService {
    private final ObjectMapper objectMapper;
    private final AuthServerProperties authServerProperties;

    @Override
    public Mono<AuthorisationServerMetadata> getAuthorizationServerMetadataFromCredentialIssuerMetadata(String processId, CredentialIssuerMetadata credentialIssuerMetadataEbsiFormat) {
        String authorizationServiceURL = credentialIssuerMetadataEbsiFormat.authorizationServer() + "/.well-known/openid-configuration";
        // get Credential Issuer Metadata
        return getAuthorizationServerMetadata(authorizationServiceURL)
                .doOnSuccess(response -> log.info("ProcessID: {} - Authorisation Server Metadata Response: {}", processId, response))
                .flatMap(this::parseCredentialIssuerMetadataResponse)
                .doOnNext(authorisationServerMetadata -> log.info("ProcessID: {} - AuthorisationServerMetadata: {}", processId, authorisationServerMetadata))
                .onErrorResume(e -> {
                    log.error("ProcessID: {} - Error while processing Authorisation Server Metadata Response from the Auth Server: {}", processId, e.getMessage());
                    return Mono.error(new RuntimeException("Error while processing Authorisation Server Metadata Response from the Auth Server"));
                });
    }

    private Mono<String> getAuthorizationServerMetadata(String authorizationServerURL) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        return getRequest(authorizationServerURL, headers)
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching Authorisation Server Metadata from the Auth Server")));
    }

    private Mono<AuthorisationServerMetadata> parseCredentialIssuerMetadataResponse(String response) {
        try {
            AuthorisationServerMetadata authorisationServerMetadata = objectMapper.readValue(response, AuthorisationServerMetadata.class);
            if (authorisationServerMetadata.tokenEndpoint().startsWith(authServerProperties.domain())){
                AuthorisationServerMetadata authorisationServerMetadataWithTokenEndpointHardcoded = AuthorisationServerMetadata.builder()
                        .issuer(authorisationServerMetadata.issuer())
                        .authorizationEndpoint(authorisationServerMetadata.authorizationEndpoint())
                        .tokenEndpoint(authServerProperties.tokenEndpoint())
                        .build();
                return Mono.just(authorisationServerMetadataWithTokenEndpointHardcoded);
            }

            else {
                // deserialize Credential Issuer Metadata
                return Mono.just(authorisationServerMetadata);
            }
        }
        catch (Exception e) {
            return Mono.error(new FailedDeserializingException("Error while deserializing Credential Issuer Metadata: " + e));
        }
    }
}
