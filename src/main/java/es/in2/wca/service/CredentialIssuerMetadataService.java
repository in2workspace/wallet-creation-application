package es.in2.wca.service;

import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.CredentialOffer;
import reactor.core.publisher.Mono;

public interface CredentialIssuerMetadataService {
    Mono<CredentialIssuerMetadata> getCredentialIssuerMetadataFromCredentialOffer(String processId, CredentialOffer credentialOffer);
}
