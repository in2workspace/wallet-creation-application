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
import es.in2.wca.domain.CredentialRequest;
import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.TokenResponse;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.exception.FailedSerializingException;
import es.in2.wca.service.CredentialService;
import id.walt.crypto.KeyAlgorithm;
import id.walt.crypto.KeyId;
import id.walt.model.DidMethod;
import id.walt.services.did.DidService;
import id.walt.services.key.KeyFormat;
import id.walt.services.key.KeyService;
import id.walt.services.keystore.KeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<CredentialResponse> getCredential(String processId, TokenResponse tokenResponse, CredentialIssuerMetadata credentialIssuerMetadata) {
        // build CredentialRequest
        return buildCredentialRequest(tokenResponse.cNonce(), credentialIssuerMetadata.credentialIssuer())
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

    private Mono<CredentialRequest> buildCredentialRequest(String nonce, String issuer) {
        // create Proof object
        return Mono.fromCallable(() -> {
                    // todo migrate to wallet-crypto
                    KeyId keyId = KeyService.Companion.getService().generate(KeyAlgorithm.ECDSA_Secp256r1);
                    String did = DidService.INSTANCE.create(DidMethod.key, keyId.getId(), null);
                    String privateKey = KeyService.Companion.getService().export(keyId.getId(), KeyFormat.JWK, KeyType.PRIVATE);
                    ECKey ecJWK = JWK.parse(privateKey).toECKey();
                    JWSSigner signer = new ECDSASigner(ecJWK);
                    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                            .type(new JOSEObjectType("openid4vci-proof+jwt"))
                            .keyID(did)
                            .build();
                    JWTClaimsSet payload = new JWTClaimsSet.Builder()
                            .audience(issuer)
                            .issueTime(java.util.Date.from(Instant.now()))
                            .claim("nonce", nonce)
                            .build();
                    SignedJWT signedJWT = new SignedJWT(header, payload);
                    signedJWT.sign(signer);
                    return signedJWT.serialize();
                })
                // create CredentialRequest object
                .flatMap(jwt -> Mono.just(CredentialRequest.builder()
                        .format("jwt_vc_json")
                        .proof(CredentialRequest.Proof.builder().proofType("jwt").jwt(jwt).build())
                        .build()));
    }

}
