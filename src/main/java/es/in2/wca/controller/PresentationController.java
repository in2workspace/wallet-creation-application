package es.in2.wca.controller;

import es.in2.wca.domain.VcSelectorResponse;
import es.in2.wca.facade.AttestationExchangeServiceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static es.in2.wca.util.Utils.getCleanBearerToken;

@Slf4j
@RestController
@RequestMapping("/api/v1/verifiable-presentation")
@RequiredArgsConstructor
public class PresentationController {

    private final AttestationExchangeServiceFacade attestationExchangeServiceFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createVerifiablePresentation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                     @RequestBody VcSelectorResponse vcSelectorResponse) {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        return getCleanBearerToken(authorizationHeader)
                .flatMap(authorizationToken ->
                        attestationExchangeServiceFacade.buildVerifiablePresentationWithSelectedVCs(processId, authorizationToken, vcSelectorResponse));
    }

}
