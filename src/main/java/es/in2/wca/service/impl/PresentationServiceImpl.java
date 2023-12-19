package es.in2.wca.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import es.in2.wca.configuration.properties.WalletCryptoProperties;
import es.in2.wca.domain.SignRequest;
import es.in2.wca.exception.EmptyCredentialListException;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.ParseErrorException;
import es.in2.wca.service.PresentationService;
import id.walt.credentials.w3c.PresentableCredential;
import id.walt.credentials.w3c.VerifiablePresentation;
import id.walt.credentials.w3c.VerifiablePresentationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static es.in2.wca.util.Utils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresentationServiceImpl implements PresentationService {
    private final WalletCryptoProperties walletCryptoProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> createVerifiablePresentation(String processId, String authorizationToken, List<PresentableCredential> verifiableCredentialsList) {
        // Get the subject DID from the first credential in the list
        return getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList)
                .flatMap(did ->
                    // Create the unsigned verifiable presentation
                    createUnsignedPresentation(verifiableCredentialsList,did)
                            .flatMap(unsignedVp -> signVerifiablePresentation(did,unsignedVp))
                )
                // Log success
                .doOnSuccess(verifiablePresentation -> log.info("ProcessID: {} - Verifiable Presentation created successfully: {}", processId, verifiablePresentation))
                // Handle errors
                .onErrorResume(e -> {
                    log.error("Error in creating Verifiable Presentation: ", e);
                    return Mono.error(e);
                });
    }

    private Mono<String> getSubjectDidFromTheFirstVcOfTheList(List<PresentableCredential> verifiableCredentialsList) {
        return Mono.fromCallable(() -> {
            // Check if the list is not empty
            try {
                if (!verifiableCredentialsList.isEmpty()) {
                    // Get the first verifiable credential's JWT and parse it
                    String verifiableCredential = verifiableCredentialsList.get(0).getVerifiableCredential().toString();
                    SignedJWT parsedVerifiableCredential = SignedJWT.parse(verifiableCredential);
                    // Extract the subject DID from the JWT claims
                    return (String) parsedVerifiableCredential.getJWTClaimsSet().getClaim("sub");
                } else {
                    // Throw an exception if the credential list is empty
                    throw new EmptyCredentialListException("Verifiable credentials list is empty");
                }
            } catch (Exception e) {
                throw new IllegalStateException("Error obtaining the subject DID from the verifiable credential" + e);
            }
        });
    }
    private Mono<String> createUnsignedPresentation(
            List<PresentableCredential> vcs,
            String holderDid)
    {
        return Mono.fromCallable(() -> {
            String id = "urn:uuid:" + UUID.randomUUID();

            VerifiablePresentationBuilder vpBuilder = new VerifiablePresentationBuilder()
                    .setId(id)
                    .setHolder(holderDid)
                    .setVerifiableCredentials(vcs);

            VerifiablePresentation vp = vpBuilder.build();

                Instant issueTime = Instant.now();
                Instant expirationTime = issueTime.plus(10, ChronoUnit.DAYS);
                Map <String, Object> vpParsed = JWTClaimsSet.parse(vp.toJson()).getClaims();
                JWTClaimsSet payload = new JWTClaimsSet.Builder()
                        .issuer(holderDid)
                        .subject(holderDid)
                        .notBeforeTime(java.util.Date.from(issueTime))
                        .expirationTime(java.util.Date.from(expirationTime))
                        .issueTime(java.util.Date.from(issueTime))
                        .jwtID(UUID.randomUUID().toString())
                        .claim("vp", vpParsed)
                        .build();
                return payload.toString();
        });
    }
    private Mono<String> signVerifiablePresentation(String did, String unsignedPresentation){
        String url = walletCryptoProperties.url() + "/api/v2/sign";
        List<Map.Entry<String, String>> headers = new ArrayList<>();
        headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        try {
            JsonNode document = objectMapper.readTree(unsignedPresentation);
            SignRequest signRequest = SignRequest.builder().did(did).document(document).documentType(JWT_VP_CLAIM).build();
            return Mono.fromCallable(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(signRequest)).doOnNext(log::info) // Logging the request body
                    // Performing the POST request
                    .flatMap(requestBody -> postRequest(url, headers, requestBody))
                    // Handling errors, if any
                    .onErrorResume(e -> {
                        log.error("Error while signing document with did: {}", did, e);
                        return Mono.error(new FailedCommunicationException("Error while signing document with did: {}" + did));
                    });
        }
        catch (JsonProcessingException e){
            log.error("Error while parsing the JWT payload", e);
            throw new ParseErrorException("Error while parsing the JWT payload: " + e.getMessage());
        }
    }
}
