package es.in2.walletcreationapplication.service;

import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import id.walt.credentials.w3c.PresentableCredential;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WalletDataCommunicationService {
    Mono<Void> saveVC(String token, String credential);
    Mono<List<PresentableCredential>> getVerifiableCredentials(VcSelectorResponseDTO vcSelectorResponseDTO, String token);
}
