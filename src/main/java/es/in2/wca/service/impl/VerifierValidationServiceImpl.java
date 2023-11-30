package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wca.exception.JwtInvalidFormatException;
import es.in2.wca.exception.ParseErrorException;
import es.in2.wca.service.VerifierValidationService;
import id.walt.model.Did;
import id.walt.services.did.DidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.in2.wca.util.Utils.ISSUER_SUB;
import static es.in2.wca.util.Utils.ISSUER_TOKEN_PROPERTY_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifierValidationServiceImpl implements VerifierValidationService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> verifyIssuerOfTheAuthorizationRequest(String processId, String jwtAuthorizationRequest) {
        // Parse the Authorization Request in JWT format
        return parseAuthorizationRequestInJwtFormat(processId, jwtAuthorizationRequest)
                // Extract and verify client_id claim from the Authorization Request
                .flatMap(signedJwt -> validateClientIdClaim(processId, signedJwt))
                .flatMap(signedJwt -> resolveDID(processId, signedJwt)
                        // Verify the Authorization Request is signed by the DID of the Relying Party
                        .flatMap(didDocument -> verifySignedJwtWithDidDocument(processId, didDocument))
                        .flatMap(jwsVerifier -> checkJWSVerifierResponse(signedJwt, jwsVerifier)
                                .doOnSuccess(v -> log.info("ProcessID: {} - Authorization Request verified successfully", processId))
                        )
                        .onErrorResume(e -> {
                            log.error("Error during the verification of Siop Auth Request on JWS format", e);
                            return Mono.error(new ParseErrorException("Error during the verification of Siop Auth Request on JWS format" + e));
                        }));
    }

    private Mono<SignedJWT> parseAuthorizationRequestInJwtFormat(String processId, String requestToken) {
        return Mono.fromCallable(() -> SignedJWT.parse(requestToken))
                .doOnSuccess(signedJWT -> log.info("ProcessID: {} - Siop Auth Request: {}", processId, signedJWT))
                .onErrorResume(e -> Mono.error(new JwtInvalidFormatException("Error parsing signed JWT " + e)));
    }

    private Mono<SignedJWT> validateClientIdClaim(String processId, SignedJWT signedJWTAuthorizationRequest) {
        Map<String, Object> jsonPayload = signedJWTAuthorizationRequest.getPayload().toJSONObject();
        String iss = jsonPayload.get(ISSUER_TOKEN_PROPERTY_NAME).toString();
        String sub = jsonPayload.get(ISSUER_SUB).toString();
        return Mono.fromCallable(() -> {
                    String authenticationRequestClaim = jsonPayload.get("auth_request").toString();
                    Pattern pattern = Pattern.compile("client_id=([^&]+)");
                    Matcher matcher = pattern.matcher(authenticationRequestClaim);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                    throw new IllegalArgumentException("client_id not found in the auth_request");
                })
                .doOnSuccess(clientId -> log.info("ProcessID: {} - client_id retrieved successfully: {}", processId, clientId))
                .flatMap(clientId -> {
                    if (!clientId.equals(iss) || !iss.equals(sub)) {
                        return Mono.error(new IllegalStateException("iss and sub MUST be the DID of the RP and must correspond to the " +
                                "client_id parameter in the Authorization Request"));
                    } else {
                        return Mono.just(signedJWTAuthorizationRequest);
                    }
                })
                .doOnSuccess(clientId -> log.info("ProcessID: {} - client_id validated successfully: {}", processId, clientId))
                .onErrorResume(e -> Mono.error(new ParseErrorException("Error parsing client_id" + e)));
    }

    private Mono<JsonNode> resolveDID(String processId, SignedJWT signedJWTAuthorizationRequest) {
        Map<String, Object> jsonPayload = signedJWTAuthorizationRequest.getPayload().toJSONObject();
        String iss = jsonPayload.get(ISSUER_TOKEN_PROPERTY_NAME).toString();
        return Mono.fromCallable(() -> {
                    Did didDocument = DidService.INSTANCE.resolve(iss, null);
                    return objectMapper.readTree(didDocument.encodePretty());
                })
                .doOnSuccess(didDocument -> log.info("ProcessID: {} - DID Document resolved successfully: {}", processId, didDocument.toPrettyString()))
                .onErrorResume(e -> Mono.error(new ParseErrorException("Error processing JSON" + e)));
    }

    private Mono<ECDSAVerifier> verifySignedJwtWithDidDocument(String processId, JsonNode didDocument) {
        return Mono.fromCallable(() -> {
                    JsonNode verificationMethod = didDocument.get("verificationMethod");
                    JsonNode verificationMethodIndex0 = verificationMethod.get(0);
                    String publicKeyJwk = verificationMethodIndex0.get("publicKeyJwk").toString();
                    return new ECDSAVerifier(ECKey.parse(publicKeyJwk).toECPublicKey());
                })
                .doOnSuccess(jwsVerifier -> log.info("ProcessID: {} - JWS Verifier generated successfully: {}", processId, jwsVerifier))
                .onErrorResume(e -> Mono.error(new ParseErrorException("Error verifying Jwt with Public EcKey" + e)));
    }

    private Mono<Void> checkJWSVerifierResponse(SignedJWT signedJWTResponse, JWSVerifier verifier) {
        try {
            if (!signedJWTResponse.verify(verifier)) {
                return Mono.error(new JwtInvalidFormatException("The 'request_token' is not valid"));
            } else {
                return Mono.empty();
            }
        } catch (Exception e) {
            return Mono.error(new ParseErrorException("Error verifying Jwt with Public EcKey" + e));
        }
    }

}
