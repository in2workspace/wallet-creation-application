package es.in2.wca.controller;


import es.in2.wca.domain.QrContent;
import es.in2.wca.service.QrCodeProcessorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static es.in2.wca.util.Utils.getCleanBearerToken;

@Tag(name = "QR Codes", description = "QR code management API")
@Slf4j
@RestController
@RequestMapping("/api/v2/execute-content")
@RequiredArgsConstructor
public class QrCodeProcessorController {

    private final QrCodeProcessorService qrCodeProcessorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Object> executeQrContent(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestBody QrContent qrContent) {
        String processId = UUID.randomUUID().toString();
        MDC.put("processId", processId);
        log.info("ProcessID: {} - Executing QR content: {}", processId, qrContent);
        return getCleanBearerToken(authorizationHeader)
                .flatMap(authorizationToken -> qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent.content()));
    }

}
