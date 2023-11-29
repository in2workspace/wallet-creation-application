package es.in2.walletcreationapplication.service;

import es.in2.walletcreationapplication.domain.VcSelectorRequestDTO;
import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import reactor.core.publisher.Mono;

public interface SiopService {
    Mono<VcSelectorRequestDTO> getSiopAuthenticationRequest(String siopAuthenticationRequestUri, String token);
    Mono<VcSelectorRequestDTO> processSiopAuthenticationRequest(String siopAuthenticationRequest, String token);
    Mono<String> sendAuthenticationResponse(VcSelectorResponseDTO vcSelectorResponseDTO,String vp);


}
