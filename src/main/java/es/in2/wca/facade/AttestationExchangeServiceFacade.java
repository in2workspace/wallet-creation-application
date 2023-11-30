package es.in2.wca.facade;

import es.in2.wca.domain.VcSelectorRequest;
import es.in2.wca.domain.VcSelectorResponse;
import reactor.core.publisher.Mono;

public interface AttestationExchangeServiceFacade {
    Mono<VcSelectorRequest> getSelectableCredentialsRequiredToBuildThePresentation(String processId, String authorizationToken, String qrContent);
    Mono<Void> buildVerifiablePresentationWithSelectedVCs(String processId, String authorizationToken, VcSelectorResponse vcSelectorResponse);
}
