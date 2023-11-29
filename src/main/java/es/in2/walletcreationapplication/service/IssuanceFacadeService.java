package es.in2.walletcreationapplication.service;

import reactor.core.publisher.Mono;

public interface IssuanceFacadeService {
    Mono<Void> getAndSaveVC(String token);
}
