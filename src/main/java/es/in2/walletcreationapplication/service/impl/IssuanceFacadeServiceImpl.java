package es.in2.walletcreationapplication.service.impl;

import es.in2.walletcreationapplication.service.IssuanceFacadeService;
import es.in2.walletcreationapplication.service.VerifiableCredentialService;
import es.in2.walletcreationapplication.service.WalletDataCommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import static es.in2.walletcreationapplication.util.Utils.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class IssuanceFacadeServiceImpl implements IssuanceFacadeService {
    private final VerifiableCredentialService verifiableCredentialService;
    private final WalletDataCommunicationService walletDataCommunicationService;


    @Override
    public Mono<Void> getAndSaveVC(String token) {
        return getUserIdFromToken(token)
                .flatMap(verifiableCredentialService::getVerifiableCredential)
                .flatMap(credential -> walletDataCommunicationService.saveVC(token,credential.getCredential()))
                .doOnSuccess(credential -> log.info("credential created and saved successfully: {}", credential))
                .doOnError(throwable -> log.error("Failed to create or save credential: {}", throwable.getMessage()));
    }
}
