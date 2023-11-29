package es.in2.walletcreationapplication.controller;

import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import es.in2.walletcreationapplication.service.SiopService;
import es.in2.walletcreationapplication.service.VerifiablePresentationService;
import es.in2.walletcreationapplication.service.WalletDataCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/vp")
@RequiredArgsConstructor
@Slf4j
public class VerifiablePresentationController {
    private final VerifiablePresentationService verifiablePresentationService;
    private final SiopService siopService;
    private final WalletDataCommunicationService walletDataCommunicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createVerifiablePresentation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody VcSelectorResponseDTO vcSelectorResponseDTO){
        log.info("Received request for getSiopAuthenticationRequest");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return walletDataCommunicationService.getVerifiableCredentials(vcSelectorResponseDTO,authorizationHeader)
                    .flatMap(presentableCredentialsList -> verifiablePresentationService.createVerifiablePresentation(presentableCredentialsList,token))
                    .flatMap(verifiablePresentation -> siopService.sendAuthenticationResponse(vcSelectorResponseDTO,verifiablePresentation));
        } else {
            return Mono.error(new IllegalArgumentException("Invalid Authorization header"));
        }
    }
}
