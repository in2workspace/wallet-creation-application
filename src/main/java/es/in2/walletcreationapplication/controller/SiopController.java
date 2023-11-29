package es.in2.walletcreationapplication.controller;

import es.in2.walletcreationapplication.domain.QrContentDTO;
import es.in2.walletcreationapplication.domain.VcSelectorRequestDTO;
import es.in2.walletcreationapplication.service.SiopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/siop")
@RequiredArgsConstructor
@Slf4j
public class SiopController {
    private final SiopService siopService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VcSelectorRequestDTO> getSiopAuthenticationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody QrContentDTO qrContentDTO) {
        log.info("Received request for getSiopAuthenticationRequest");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return siopService.getSiopAuthenticationRequest(qrContentDTO.getContent(),token);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid Authorization header"));
        }
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VcSelectorRequestDTO> processSiopAuthenticationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody QrContentDTO qrContentDTO) {
        log.info("Received request for processSiopAuthenticationRequest");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return siopService.processSiopAuthenticationRequest(qrContentDTO.getContent(),token);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid Authorization header"));
        }
    }
}
