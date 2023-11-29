package es.in2.walletcreationapplication.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import es.in2.walletcreationapplication.config.CacheConfig;
import es.in2.walletcreationapplication.domain.*;
import es.in2.walletcreationapplication.exception.FailedCommunicationException;
import es.in2.walletcreationapplication.exception.FailedDeserializingException;
import es.in2.walletcreationapplication.exception.FailedSerializingException;
import es.in2.walletcreationapplication.service.VerifiableCredentialService;
import id.walt.crypto.KeyAlgorithm;
import id.walt.crypto.KeyId;
import id.walt.model.DidMethod;
import id.walt.services.did.DidService;
import id.walt.services.key.KeyFormat;
import id.walt.services.key.KeyService;
import id.walt.services.keystore.KeyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSHeader;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static es.in2.walletcreationapplication.util.Utils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifiableCredentialServiceImpl implements VerifiableCredentialService {
    @Value("${app.url.wallet-crypto}")
    private String urlWalletCrypto;
    private final ObjectMapper objectMapper;
    private final CacheConfig cacheConfig;

    @Override
    public Mono<Void> getCredentialIssuerMetadata(String credentialOfferUriExtended, String userId) {
        return getCredentialOffer(credentialOfferUriExtended)
                .flatMap(credentialOfferForPreAuthorizedCodeFlow -> getCredentialIssuerMetadataObject(credentialOfferForPreAuthorizedCodeFlow)
                        .flatMap(issuerMetadata -> getAccessTokenAndNonce(credentialOfferForPreAuthorizedCodeFlow,issuerMetadata)
                                .flatMap(accessTokenAndNonce-> saveIssuanceData(issuerMetadata,accessTokenAndNonce,userId)))
                ).doOnSuccess(aVoid -> log.info("All data saved and retrieved correctly"))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while fetching the data for issuance flow", e);
                    return Mono.error(new FailedCommunicationException("Error while fetching the data for issuance flow"));
                });
    }

    @Override
    public Mono<VerifiableCredentialResponseDTO> getVerifiableCredential(String userId) {
        return getIssuanceData(userId)
                .flatMap(issuanceRequestData -> createAndAddJwtToCredentialRequestBody(issuanceRequestData.getNonce(),issuanceRequestData.getMetadata().getCredentialIssuer())
                        .flatMap(credentialRequestBody -> getVerifiableCredential(issuanceRequestData.getAccessToken(),issuanceRequestData.getMetadata().getCredentialEndpoint(),credentialRequestBody)))
                .doOnSuccess(credential -> log.debug("Credential retrieved successfully" + credential.getCredential()))
                .onErrorResume(e -> {
                    log.error("Error while retrieving the credential", e);
                    return Mono.error(new FailedCommunicationException("Error while retrieving the credential"));
                });
    }
    private Mono<Void> saveIssuanceData(CredentialIssuerMetadata issuerMetadata, List<String> accessTokenAndNonce, String userId) {
        IssuanceRequestData issuanceRequestData = IssuanceRequestData.builder()
                .metadata(issuerMetadata)
                .nonce(accessTokenAndNonce.get(0))
                .accessToken(accessTokenAndNonce.get(1))
                .build();
        return cacheConfig.cacheService().addIssuanceRequestData(userId, issuanceRequestData)
                .then();
    }
    private Mono<IssuanceRequestData> getIssuanceData(String userId) {
        return cacheConfig.cacheService().getIssuanceRequestData(userId);
    }


    private Mono<CredentialOfferForPreAuthorizedCodeFlow> getCredentialOffer(String credentialOfferUriExtended) {
        return getCredentialOfferUri(credentialOfferUriExtended)
                .flatMap(this::fetchAndProcessCredentialOffer)
                .doOnNext(credentialOffer -> log.debug("Credential offer: {}", credentialOffer))
                .onErrorResume(e -> {
                    log.error("Error while processing credentialOffer from the issuer", e);
                    return Mono.error(new RuntimeException("Error while processing credentialOffer from the issuer"));
                });
    }
    private Mono<String> getCredentialOfferUri(String credentialOfferUriExtended) {
        return Mono.fromCallable(() -> {
            String[] splitCredentialOfferUri = credentialOfferUriExtended.split("=");
            String credentialOfferUriValue = splitCredentialOfferUri[1];
            return URLDecoder.decode(credentialOfferUriValue, StandardCharsets.UTF_8);
        }).doOnNext(uri -> log.debug("Credential offer URI: {}", uri));
    }

    private Mono<CredentialOfferForPreAuthorizedCodeFlow>  fetchAndProcessCredentialOffer(String credentialOfferUri) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));
        return getRequest(credentialOfferUri, headers)
                .flatMap(response -> {
                    try {
                        log.info(response);
                        CredentialOfferForPreAuthorizedCodeFlow credentialOffer = objectMapper.readValue(response, CredentialOfferForPreAuthorizedCodeFlow.class);
                        return Mono.just(credentialOffer);
                    } catch (Exception e) {
                        log.error("Error while deserializing CredentialOfferForPreAuthorizedCodeFlow: " + response, e);
                        return Mono.error(new FailedDeserializingException("Error while deserializing CredentialOfferForPreAuthorizedCodeFlow: " + e));
                    }
                    // Replace with actual logic
                })
                .doOnNext(credentialOffer -> log.debug("Credential offer: {}", credentialOffer))
                // Using onErrorResume to handle other errors that may occur during the GET request
                .onErrorResume(e -> {
                    log.error("Error while fetching credentialOffer from the issuer", e);
                    return Mono.error(new FailedCommunicationException("Error while fetching credentialOffer from the issuer"));
                });
    }

    private Mono<CredentialIssuerMetadata> getCredentialIssuerMetadataObject(CredentialOfferForPreAuthorizedCodeFlow credentialOffer) {
        return getCredentialIssuerMetadataUri(credentialOffer)
                .flatMap(this::fetchAndProcessCredentialIssuerMetadata)
                .doOnNext(metadata -> log.debug("Credential Issuer Metadata: {}", metadata))
                .onErrorResume(e -> {
                    log.error("Error while processing CredentialIssuerMetadata from the issuer", e);
                    return Mono.error(new RuntimeException("Error while processing CredentialIssuerMetadata from the issuer"));
                });
    }
    private Mono<String> getCredentialIssuerMetadataUri(CredentialOfferForPreAuthorizedCodeFlow credentialOffer) {
        return Mono.just(credentialOffer.getCredentialIssuer() + "/.well-known/openid-credential-issuer");
    }

    private Mono<CredentialIssuerMetadata> fetchAndProcessCredentialIssuerMetadata(String credentialIssuerMetadataUri) {
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));
        return getRequest(credentialIssuerMetadataUri, headers)
                .flatMap(response -> {
                    try {
                        log.info(response);
                        CredentialIssuerMetadata credentialIssuerMetadata = objectMapper.readValue(response, CredentialIssuerMetadata.class);
                        return Mono.just(credentialIssuerMetadata);
                    } catch (Exception e) {
                        log.error("Error while deserializing CredentialIssuerMetadata: " + response, e);
                        return Mono.error(new FailedDeserializingException("Error while deserializing CredentialIssuerMetadata: " + e));
                    }
                })

                .doOnNext(metadata -> log.debug("Credential Issuer Metadata: {}", metadata))
                .onErrorResume(e -> {
                    log.error("Error while fetching CredentialIssuerMetadata from the issuer", e);
                    return Mono.error(new FailedCommunicationException("Error while fetching CredentialIssuerMetadata from the issuer"));
                });
    }

    private Mono<List<String>> getAccessTokenAndNonce(CredentialOfferForPreAuthorizedCodeFlow credentialOffer, CredentialIssuerMetadata credentialIssuerMetadata) {
        String tokenEndpoint = credentialIssuerMetadata.getCredentialToken();
        // Assuming getPreAuthorizedCode() and other methods are available
        String preAuthorizedCode = credentialOffer.getGrants().get(PRE_AUTH_CODE_GRANT_TYPE).getPreAuthorizedCode();

        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));
        Map<String, String> formData = Map.of("grant_type", PRE_AUTH_CODE_GRANT_TYPE, "pre-authorized_code", preAuthorizedCode);
        return buildUrlEncodedFormDataRequestBody(formData)
                .flatMap(body -> postRequest(tokenEndpoint, headers, body))
                .flatMap(response -> {
                    try {
                        JsonNode accessTokenAndNonceResponse = objectMapper.readTree(response);
                        String accessToken = accessTokenAndNonceResponse.get("access_token").asText();
                        String cNonce = accessTokenAndNonceResponse.get("c_nonce").asText();
                        return Mono.just(List.of(cNonce, accessToken));
                    } catch (Exception e) {
                        log.error("Error while deserializing AccessToken and Nonce from the issuer", e);
                        return Mono.error(new FailedDeserializingException("Error while deserializing accessTokenAndNonceResponse: " + e));
                    }
                })
                .doOnNext(tokenAndNonce -> log.debug("Access token and nonce value: {}", tokenAndNonce))
                .onErrorResume(e -> {
                    log.error("Error while fetching AccessTokenAndNonce from the issuer", e);
                    return Mono.error(new FailedCommunicationException("Error while fetching AccessTokenAndNonce from the issuer" + e));
                });
    }

    private Mono<VerifiableCredentialResponseDTO> getVerifiableCredential(
            String accessToken,
            String credentialEndpoint,
            CredentialRequestBodyDTO credentialRequestBodyDTO) {

        List<Map.Entry<String, String>> headers = List.of(
                Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON),
                Map.entry(HEADER_AUTHORIZATION, "Bearer " + accessToken)
        );
        try {
            String requestBodyJson = objectMapper.writeValueAsString(credentialRequestBodyDTO);

            return postRequest(credentialEndpoint, headers, requestBodyJson)
                    .flatMap(response -> {
                        try {
                            VerifiableCredentialResponseDTO verifiableCredential = objectMapper.readValue(response, VerifiableCredentialResponseDTO.class);
                            log.debug(verifiableCredential.getCredential());
                            return Mono.just(verifiableCredential);
                        } catch (Exception e) {
                            log.error("Error while deserializing VerifiableCredentialResponse from the issuer", e);
                            return Mono.error(new FailedDeserializingException("Error while deserializing VerifiableCredentialResponse: " + response));
                        }
                    })
                    .doOnNext(response -> log.debug("Verifiable credential: {}", response))
                    .onErrorResume(e -> {
                        log.error("Error while fetching  VerifiableCredentialResponse from the issuer", e);
                        return Mono.error(new FailedCommunicationException("Error while fetching  VerifiableCredentialResponse from the issuer"));
                    });
        } catch (Exception e) {
            log.error("Error while serializing credentialRequestBodyDTO", e);
            return Mono.error(new FailedSerializingException("Error while serializing credentialRequestBodyDTO"));
        }
    }

    private Mono<CredentialRequestBodyDTO> createAndAddJwtToCredentialRequestBody(String nonce, String issuer) {
//        String url = urlWalletCrypto + "/api/v1/credentials?did=" + credentialRequestDTO.getDid();
//        List<Map.Entry<String, String>> headers = new ArrayList<>();
//
//        return getRequest(url, headers)
//                .flatMap(response -> {
//                    try {
//                        ECKey ecJWK = JWK.parse(response).toECKey();
//                        log.debug("ECKey: {}", ecJWK);
//
//                        JWSSigner signer = new ECDSASigner(ecJWK);
//
//                        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
//                                .type(new JOSEObjectType("openid4vci-proof+jwt"))
//                                .keyID(credentialRequestDTO.getDid())
//                                .build();
//                        Instant issueTime = Instant.now();
//                        JWTClaimsSet payload = new JWTClaimsSet.Builder()
//                                .audience(issuer)
//                                .issueTime(java.util.Date.from(issueTime))
//                                .claim("nonce", nonce)
//                                .build();
//
//                        SignedJWT signedJWT = new SignedJWT(header, payload);
//                        signedJWT.sign(signer);
//                        log.debug("JWT signed successfully");
//                        return Mono.just(signedJWT.serialize());
//                    } catch (Exception e) {
//                        log.error("Error while creating the Signed JWT", e);
//                        return Mono.error(new RuntimeException("Error while deserializing VerifiableCredentialResponse: " + response));
//                    }
//                })
//                .flatMap(jwt -> {
//                    ProofDTO proof = ProofDTO.builder().proofType("jwt").jwt(jwt).build();
//                    return Mono.just(CredentialRequestBodyDTO.builder().format("jwt_vc_json").proof(proof).build());
//                })
//                .doOnNext(requestBody -> log.debug("Credential Request Body: {}", requestBody))
//                .onErrorResume(e -> {
//                    log.error("Error creating CredentialRequestBodyDTO", e);
//                    return Mono.error(new RuntimeException("Error creating CredentialRequestBodyDTO", e));
//                });
        return Mono.fromCallable(() -> {
                    KeyId keyId = KeyService.Companion.getService().generate(KeyAlgorithm.ECDSA_Secp256r1);
                    String did = DidService.INSTANCE.create(DidMethod.key, keyId.getId(),null);
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
                .flatMap(jwt -> {
                    ProofDTO proof = ProofDTO.builder().proofType("jwt").jwt(jwt).build();
                    CredentialRequestBodyDTO credentialRequestBody = CredentialRequestBodyDTO.builder()
                            .format("jwt_vc_json")
                            .proof(proof)
                            .build();
                    return Mono.just(credentialRequestBody);
                });
    }
}
