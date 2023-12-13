package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wca.configuration.properties.WalletCryptoProperties;
import es.in2.wca.domain.*;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.exception.FailedSerializingException;
import es.in2.wca.service.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final ObjectMapper objectMapper;
    private  final WalletCryptoProperties walletCryptoProperties;

    @Override
    public Mono<CredentialResponse> getCredential(String processId, TokenResponse tokenResponse, CredentialIssuerMetadata credentialIssuerMetadata, String authorizationToken) {
        // build CredentialRequest
        return generateDid(authorizationToken)
                .flatMap(did -> buildCredentialRequest(tokenResponse.cNonce(), credentialIssuerMetadata.credentialIssuer(),did))
                .doOnSuccess(credentialRequest -> log.info("ProcessID: {} - CredentialRequest: {}", processId, credentialRequest))
                // post CredentialRequest
                .flatMap(credentialRequest -> postCredential(tokenResponse, credentialIssuerMetadata, credentialRequest))
                .doOnSuccess(response -> log.info("ProcessID: {} - Credential Post Response: {}", processId, response))
                // deserialize CredentialResponse
                .flatMap(response -> {
                    try {
                        CredentialResponse credentialResponse = objectMapper.readValue(response, CredentialResponse.class);
                        return Mono.just(credentialResponse);
                    } catch (Exception e) {
                        log.error("Error while deserializing CredentialResponse from the issuer", e);
                        return Mono.error(new FailedDeserializingException("Error while deserializing CredentialResponse: " + response));
                    }
                })
                .doOnSuccess(credentialResponse -> log.info("ProcessID: {} - CredentialResponse: {}", processId, credentialResponse))
                .onErrorResume(e -> {
                    log.error("Error while fetching Credential from Issuer: {}", e.getMessage());
                    return Mono.error(new FailedCommunicationException("Error while fetching Credential from Issuer"));
                });
    }

    private Mono<String> postCredential(TokenResponse tokenResponse, CredentialIssuerMetadata credentialIssuerMetadata,
                                        CredentialRequest credentialRequest) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON),
                Map.entry(HEADER_AUTHORIZATION, BEARER + tokenResponse.accessToken()));
        try {
            return postRequest(credentialIssuerMetadata.credentialEndpoint(), headers, objectMapper.writeValueAsString(credentialRequest))
                    .onErrorResume(e -> {
                        log.error("Error while fetching Credential from Issuer: {}", e.getMessage());
                        return Mono.error(new FailedCommunicationException("Error while fetching  Credential from Issuer"));
                    });
        } catch (Exception e) {
            log.error("Error while serializing CredentialRequest: {}", e.getMessage());
            return Mono.error(new FailedSerializingException("Error while serializing Credential Request"));
        }
    }


    private Mono<String> generateDid(String authorizationToken) {
        // Create dynamic URL
        String walletCryptoUrl = walletCryptoProperties.url() + "/api/v2/dids/key";
        // Add headers
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken));
        // Send request
        return postRequest(walletCryptoUrl,headers,"");
    }
    private Mono<CredentialRequest> buildCredentialRequest(String nonce, String issuer, String did) {
        String url = walletCryptoProperties.url() + "/api/v2/secrets?did=" + did;
        List<Map.Entry<String, String>> headers = new ArrayList<>();

        return getRequest(url, headers)
                .flatMap(response -> {
                    try {
                        ECKey ecJWK = JWK.parse(response).toECKey();
                        log.debug("ECKey: {}", ecJWK);

                        JWSSigner signer = new ECDSASigner(ecJWK);

                        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                                .type(new JOSEObjectType("openid4vci-proof+jwt"))
                                .keyID(did)
                                .build();
                        Instant issueTime = Instant.now();
                        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                                .audience(issuer)
                                .issueTime(java.util.Date.from(issueTime))
                                .claim("nonce", nonce)
                                .build();

                        SignedJWT signedJWT = new SignedJWT(header, payload);
                        signedJWT.sign(signer);
                        log.debug("JWT signed successfully");
                        return Mono.just(signedJWT.serialize());
                    } catch (Exception e) {
                        log.error("Error while creating the Signed JWT", e);
                        return Mono.error(new RuntimeException("Error while deserializing VerifiableCredentialResponse: " + response));
                    }
                })
                .flatMap(jwt -> Mono.just(CredentialRequest.builder()
                .format("jwt_vc_json")
                .proof(CredentialRequest.Proof.builder().proofType("jwt").jwt(jwt).build())
                .build()))
                .doOnNext(requestBody -> log.debug("Credential Request Body: {}", requestBody))
                .onErrorResume(e -> {
                    log.error("Error creating CredentialRequestBodyDTO", e);
                    return Mono.error(new RuntimeException("Error creating CredentialRequestBodyDTO", e));
                });
    }

}
