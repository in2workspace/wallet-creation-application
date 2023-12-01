package es.in2.wca.service.impl;

import com.nimbusds.jose.JWSObject;
import es.in2.wca.domain.AuthorizationRequest;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.service.AuthorizationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationRequestServiceImpl implements AuthorizationRequestService {

    @Override
    public Mono<String> getAuthorizationRequestFromVcLoginRequest(String processId, String qrContent) {
        log.info("Processing a Verifiable Credential Login Request");
        // Get Authorization Request executing the VC Login Request
        return getJwtAuthorizationRequest(qrContent)
                .doOnSuccess(response -> log.info("ProcessID: {} - Authorization Request Response: {}", processId, response))
                .onErrorResume(e -> {
                    log.error("ProcessID: {} - Error while processing Authorization Request from the Issuer: {}", processId, e.getMessage());
                    return Mono.error(new RuntimeException("Error while processing Authorization Request from the Issuer"));
                });
    }

    private Mono<String> getJwtAuthorizationRequest(String authorizationRequestUri) {
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        return getRequest(authorizationRequestUri, headers)
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching Authorization Request")));
    }

    @Override
    public Mono<AuthorizationRequest> getAuthorizationRequestFromJwtAuthorizationRequestClaim(String processId, String jwtAuthorizationRequestClaim) {
        try {
            JWSObject jwsObject = JWSObject.parse(jwtAuthorizationRequestClaim);
            String authorizationRequestClaim = jwsObject.getPayload().toJSONObject().get("auth_request").toString();
            return Mono.fromCallable(() -> AuthorizationRequest.fromString(authorizationRequestClaim));
        } catch (Exception e) {
            log.error("ProcessID: {} - Error while parsing Authorization Request: {}", processId, e.getMessage());
            return Mono.error(new RuntimeException("Error while parsing Authorization Request"));
        }
    }

}
