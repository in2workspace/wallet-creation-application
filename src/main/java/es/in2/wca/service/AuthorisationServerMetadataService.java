package es.in2.wca.service;

import es.in2.wca.domain.AuthorisationServerMetadata;
import es.in2.wca.domain.CredentialIssuerMetadata;
import reactor.core.publisher.Mono;

public interface AuthorisationServerMetadataService {
    Mono<AuthorisationServerMetadata> getAuthorizationServerMetadataFromCredentialIssuerMetadata(String processId, CredentialIssuerMetadata credentialIssuerMetadata);
}