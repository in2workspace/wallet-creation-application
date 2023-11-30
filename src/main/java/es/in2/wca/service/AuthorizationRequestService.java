package es.in2.wca.service;

import es.in2.wca.domain.AuthorizationRequest;
import reactor.core.publisher.Mono;

public interface AuthorizationRequestService {
    Mono<String> getAuthorizationRequestFromVcLoginRequest(String processId, String qrContent, String authorizationToken);

    Mono<AuthorizationRequest> getAuthorizationRequestFromJwtAuthorizationRequestClaim(String processId, String jwtAuthorizationRequestClaim);
}
