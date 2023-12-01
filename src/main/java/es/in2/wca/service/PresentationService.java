package es.in2.wca.service;

import id.walt.credentials.w3c.PresentableCredential;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PresentationService {
    Mono<String> createVerifiablePresentation(String processId, String authorizationToken, List<PresentableCredential> verifiableCredentialsList);
}
