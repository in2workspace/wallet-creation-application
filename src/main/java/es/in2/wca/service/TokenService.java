package es.in2.wca.service;

import es.in2.wca.domain.AuthorisationServerMetadata;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.domain.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {
    Mono<TokenResponse> getPreAuthorizedToken(String processId, CredentialOffer credentialOffer, AuthorisationServerMetadata authorisationServerMetadata);
}
