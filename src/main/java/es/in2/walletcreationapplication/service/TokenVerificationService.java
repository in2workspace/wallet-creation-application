package es.in2.walletcreationapplication.service;

import reactor.core.publisher.Mono;

public interface TokenVerificationService {
    Mono<Void> verifySiopAuthRequestAsJwsFormat(String requestToken);
}
