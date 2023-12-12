package es.in2.wca.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.configuration.properties.WalletDataProperties;
import es.in2.wca.domain.AuthorizationRequest;
import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.VcSelectorResponse;
import es.in2.wca.domain.CredentialSaveRequest;
import es.in2.wca.domain.CredentialsBasicInfo;
import es.in2.wca.domain.SelectableVCsRequest;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.exception.ParseErrorException;
import es.in2.wca.service.WalletDataService;
import id.walt.credentials.w3c.PresentableCredential;
import id.walt.credentials.w3c.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletDataServiceImpl implements WalletDataService {

    private final ObjectMapper objectMapper;

    private final WalletDataProperties walletDataProperties;

    @Override
    public Mono<CredentialResponse> saveCredential(String processId, String authorizationToken, CredentialResponse credentialResponse) {
        return save(authorizationToken, credentialResponse)
                .doOnSuccess(result -> log.info("ProcessId: {}, Credential saved successfully", processId))
                .doOnError(e -> log.error("ProcessId: {}, Error while saving credential: {}", processId, e.getMessage()))
                .flatMap(result -> Mono.just(credentialResponse));
    }

    private Mono<String> save(String authorizationToken, CredentialResponse credentialResponse) {
        // Create dynamic URL
        String walletDataVCUrl = walletDataProperties.url() + "/api/v2/credentials";
        // Add headers
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken));
        headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        // Add body
        CredentialSaveRequest credentialSaveRequest = CredentialSaveRequest.builder()
                .credential(credentialResponse.credential())
                .build();
        // Send request
        try {
            return postRequest(walletDataVCUrl, headers, objectMapper.writeValueAsString(credentialSaveRequest))
                    .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching credentialOffer from the issuer")));
        } catch (JsonProcessingException e) {
            return Mono.error(new ParseErrorException("Error converting CredentialSaveRequest to JSON"));
        }
    }

    @Override
    public Mono<List<CredentialsBasicInfo>> getSelectableVCsByAuthorizationRequestScope(String processId, String authorizationToken, AuthorizationRequest authorizationRequest) {

        return getCredentialsByScope(processId, authorizationRequest, authorizationToken)
                .doOnSuccess(selectableVCs -> log.info("ProcessID: {} - SelectableVCs: {}", processId, selectableVCs))
                .flatMap(this::parseSelectableVCsResponse)
                .doOnSuccess(selectableVCs -> log.info("ProcessID: {} - SelectableVCs: {}", processId, selectableVCs))
                .doOnError(e -> log.error("ProcessID: {} - Error getting selectable VCs: {}", processId, e.getMessage()));
    }

    private Mono<String> getCredentialsByScope(String processId, AuthorizationRequest authorizationRequest, String authorizationToken) {
        try {
            // URL
            String url = walletDataProperties.url() + GET_SELECTABLE_VCS;
            // Headers
            List<Map.Entry<String, String>> headers = new ArrayList<>();
            headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken));
            headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            // Body
            String body = objectMapper.writeValueAsString(SelectableVCsRequest.builder().vcTypes(authorizationRequest.scope()).build());
            return postRequest(url, headers, body);
        } catch (Exception e) {
            log.error("ProcessID: {} - Error while fetching selectableVCs: {}", processId, e.getMessage());
            return Mono.error(new FailedCommunicationException("Error while fetching selectableVCs: " + e));
        }
    }

    private Mono<List<CredentialsBasicInfo>> parseSelectableVCsResponse(String response) {
        try {
            return Mono.fromCallable(() -> objectMapper.readValue(response, new TypeReference<>() {
            }));
        } catch (Exception e) {
            log.error("Error while fetching selectableVCs: " + response, e);
            return Mono.error(new FailedDeserializingException("Error while fetching selectableVCs:: " + e));
        }
    }

    @Override
    public Mono<List<PresentableCredential>> getVerifiableCredentials(String processId, String authorizationToken, VcSelectorResponse vcSelectorResponse) {
        return Flux.fromIterable(vcSelectorResponse.selectedVcList())
                .flatMap(verifiableCredential -> {
                    // Headers
                    List<Map.Entry<String, String>> headers = new ArrayList<>();
                    headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken));
                    // Wallet Data URL
                    String url = walletDataProperties.url() + "/api/v2/credentials/id?credentialId=" + verifiableCredential.id() + "&format=vc_jwt";
                    return getRequest(url, headers)
                            .map(response -> new PresentableCredential(VerifiableCredential.Companion.fromString(response), null, false));
                }).collectList();
    }

}
