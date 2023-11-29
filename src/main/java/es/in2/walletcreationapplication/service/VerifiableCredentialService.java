package es.in2.walletcreationapplication.service;

import es.in2.walletcreationapplication.domain.VerifiableCredentialResponseDTO;
import reactor.core.publisher.Mono;

public interface VerifiableCredentialService {
    Mono<Void> getCredentialIssuerMetadata(String credentialOfferUriExtended, String token);
    Mono<VerifiableCredentialResponseDTO> getVerifiableCredential(String token);

}
