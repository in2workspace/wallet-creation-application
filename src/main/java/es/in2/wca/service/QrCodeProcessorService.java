package es.in2.wca.service;

import reactor.core.publisher.Mono;

public interface QrCodeProcessorService {
    Mono<Object> processQrContent(String processId, String authorizationToken, String qrContent);
}
