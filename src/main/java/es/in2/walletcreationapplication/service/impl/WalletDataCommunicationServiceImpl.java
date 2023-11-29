package es.in2.walletcreationapplication.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.walletcreationapplication.domain.VCRequestDTO;
import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import es.in2.walletcreationapplication.exception.ParseErrorException;
import es.in2.walletcreationapplication.service.WalletDataCommunicationService;
import id.walt.credentials.w3c.PresentableCredential;
import id.walt.credentials.w3c.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.walletcreationapplication.util.Utils.getRequest;
import static es.in2.walletcreationapplication.util.Utils.postRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletDataCommunicationServiceImpl implements WalletDataCommunicationService {
    @Value("${app.url.wallet-data}")
    private String urlWalletData;
    @Override
    public Mono<Void> saveVC(String token, String credential) {
        String walletDataVCUrl = urlWalletData + "/api/credentials";
        log.debug(urlWalletData);
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, token));
        headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        VCRequestDTO vcRequestDTO = VCRequestDTO.builder().credential(credential).build();
        log.debug(vcRequestDTO.getCredential());
        try {
            String body = new ObjectMapper().writeValueAsString(vcRequestDTO);

            return postRequest(walletDataVCUrl, headers, body)
                    .then()
                    .doOnSuccess(result -> log.debug("VC persisted successfully"))
                    .doOnError(throwable -> log.error("Error: {}", throwable.getMessage()));
        } catch (JsonProcessingException e) {
            return Mono.error(new ParseErrorException("Error converting VC request to JSON"));
        }
    }
    @Override
    public Mono<List<PresentableCredential>> getVerifiableCredentials(VcSelectorResponseDTO vcSelectorResponseDTO, String token) {
        return Flux.fromIterable(vcSelectorResponseDTO.getSelectedVcList())
                .flatMap(vc -> {
                    List<Map.Entry<String, String>> headers = new ArrayList<>();
                    headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, token));
                    String url = urlWalletData + "/api/credentials/id?credentialId=" + vc.getId() + "&format=vc_jwt";
                    return getRequest(url, headers)
                            .map(response -> new PresentableCredential(VerifiableCredential.Companion.fromString(response), null, false));
                })
                .collectList();
    }
}
