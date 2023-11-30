package es.in2.wca.service;

import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.TokenResponse;
import reactor.core.publisher.Mono;

public interface CredentialService {
    Mono<CredentialResponse> getCredential(String processId, TokenResponse tokenResponse, CredentialIssuerMetadata credentialIssuerMetadata);
}
