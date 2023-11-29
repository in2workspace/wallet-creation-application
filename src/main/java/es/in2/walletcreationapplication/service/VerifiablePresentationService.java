package es.in2.walletcreationapplication.service;

import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import id.walt.credentials.w3c.PresentableCredential;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VerifiablePresentationService {
    Mono<String> createVerifiablePresentation(List<PresentableCredential> verifiableCredentialsList, String token);
}
