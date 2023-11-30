package es.in2.wca.facade;

import es.in2.wca.domain.CredentialResponse;
import reactor.core.publisher.Mono;

public interface CredentialIssuanceServiceFacade {
    Mono<CredentialResponse> getCredential(String processId, String authorizationToken, String qrContent);
}
