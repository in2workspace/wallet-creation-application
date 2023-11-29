package es.in2.walletcreationapplication.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.SignedJWT;
import es.in2.walletcreationapplication.exception.JwtInvalidFormatException;
import es.in2.walletcreationapplication.exception.ParseErrorException;
import es.in2.walletcreationapplication.service.TokenVerificationService;
import id.walt.model.Did;
import id.walt.services.did.DidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.interfaces.ECPublicKey;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.in2.walletcreationapplication.util.Utils.ISSUER_SUB;
import static es.in2.walletcreationapplication.util.Utils.ISSUER_TOKEN_PROPERTY_NAME;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenVerificationServiceImpl implements TokenVerificationService {
    private  final ObjectMapper objectMapper;
    @Override
    public Mono<Void> verifySiopAuthRequestAsJwsFormat(String requestToken) {
        log.info("TokenVerificationServiceImpl - verifySiopAuthRequestAsJwsFormat()");
        return parseRequestTokenToSignedJwt(requestToken)
                .flatMap(signedJWT -> resolveDID(signedJWT.getPayload())
                        .flatMap(this::generateEcPublicKeyFromDidDocument)
                        .flatMap(this::verifySignedJwtWithPublicEcKey)
                        .flatMap(jwsVerifier -> checkJWSVerifierResponse(signedJWT,jwsVerifier)))
                .then()
                .onErrorResume(e -> {
                    log.error("Error during the verification of Siop Auth Request on JWS format", e);
                    return Mono.error(new ParseErrorException("Error during the verification of Siop Auth Request on JWS format" + e));
                });
    }
    private Mono<SignedJWT> parseRequestTokenToSignedJwt(String requestToken) {
        return Mono.fromCallable(() -> SignedJWT.parse(requestToken))
                .onErrorResume(e -> Mono.error(new JwtInvalidFormatException("Error parsing signed JWT " + e)));
    }

    private Mono<JsonNode> resolveDID(Payload payload) {
        Map<String, Object> jsonPayload = payload.toJSONObject();
        String issuerDid = jsonPayload.get(ISSUER_TOKEN_PROPERTY_NAME).toString();
        String sub = jsonPayload.get(ISSUER_SUB).toString();

        return getClientId(jsonPayload)
                .flatMap(clientId -> {
                    log.info("clientId: {}", clientId);
                    if (!clientId.equals(issuerDid) || !issuerDid.equals(sub)) {
                        return Mono.error(new IllegalStateException("iss and sub MUST be the DID of the RP and must correspond to the client_id parameter in the Authorization Request"));
                    }
                    log.info("issuer_did = {}", issuerDid);
                    log.info("Resolving DID using SSI Kit");
                    Did didDocument = DidService.INSTANCE.resolve(issuerDid, null);
                    log.debug("didDocument = {}", didDocument.encodePretty());

                    return Mono.fromCallable(() -> objectMapper.readTree(didDocument.encodePretty()))
                            .onErrorResume(e -> Mono.error(new ParseErrorException("Error processing JSON" + e)));
                });
    }
    private Mono<String> getClientId(Map<String, Object> siopAuthenticationRequest) {
        return Mono.fromCallable(() -> {
                    String authRequest = siopAuthenticationRequest.get("auth_request").toString();
                    Pattern pattern = Pattern.compile("client_id=([^&]+)");
                    Matcher matcher = pattern.matcher(authRequest);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                    throw new IllegalArgumentException("client_id not found in the auth_request");
                })
                .onErrorResume(e -> {
                    log.error("Error parsing client_id", e);
                    return Mono.error(new ParseErrorException("Error parsing client_id" + e));
                });
    }
    private Mono<ECPublicKey> generateEcPublicKeyFromDidDocument(JsonNode didDocument) {
        return Mono.fromCallable(() -> {
            log.info("TokenVerificationServiceImpl - generateEcPublicKeyFromDidDocument()");
            JsonNode verificationMethod = didDocument.get("verificationMethod");
            JsonNode verificationMethodIndex0 = verificationMethod.get(0);
            String publicKeyJwk = verificationMethodIndex0.get("publicKeyJwk").toString();
            return ECKey.parse(publicKeyJwk).toECPublicKey();
        });
    }
    private Mono<JWSVerifier> verifySignedJwtWithPublicEcKey(ECPublicKey ecPublicKey) {
        return Mono.fromCallable(() -> {
            log.info("TokenVerificationServiceImpl - verifySignedJwtWithPublicEcKey()");
            return (JWSVerifier) new ECDSAVerifier(ecPublicKey);
        }).onErrorResume(e -> {
            log.error("Error verifiying Jwt with Public EcKey", e);
            return Mono.error(new ParseErrorException("Error verifiying Jwt with Public EcKey" + e));
        });
    }
    private Mono<Void> checkJWSVerifierResponse(SignedJWT signedJWTResponse, JWSVerifier verifier) {
        return Mono.fromCallable(() -> {
            log.info("TokenVerificationServiceImpl - checkJWSVerifierResponse()");
            if (!signedJWTResponse.verify(verifier)) {
                throw new JwtInvalidFormatException("The 'request_token' is not valid");
            }
            return null;
        });
    }

}
