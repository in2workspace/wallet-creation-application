package es.in2.walletcreationapplication.service.impl;

import com.nimbusds.jwt.SignedJWT;
import es.in2.walletcreationapplication.exception.EmptyCredentialListException;
import es.in2.walletcreationapplication.service.VerifiablePresentationService;
import id.walt.credentials.w3c.PresentableCredential;
import id.walt.crypto.KeyAlgorithm;
import id.walt.crypto.KeyId;
import id.walt.custodian.Custodian;
import id.walt.model.DidMethod;
import id.walt.services.did.DidService;
import id.walt.services.key.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifiablePresentationServiceImpl implements VerifiablePresentationService {
    @Override
    public Mono<String> createVerifiablePresentation(List<PresentableCredential> verifiableCredentialsList, String token) {
        // Get the subject DID from the first credential in the list
        return getSubjectDidFromTheFirstVcOfTheList(verifiableCredentialsList)
                .flatMap(subjectDid -> {
                    log.debug("subject DID = {}", subjectDid);
                    // Generate a new key for signing the presentation
                    KeyId keyId = KeyService.Companion.getService().generate(KeyAlgorithm.ECDSA_Secp256r1);
                    log.debug("KeyId = {}", keyId);
                    // Create a holder DID
                    String holderDid = DidService.INSTANCE.create(DidMethod.key, keyId.getId(), null);
                    long secondsToAdd = 60000L;
                    // Create the verifiable presentation
                    return Mono.just(Custodian.Companion.getService().createPresentation(verifiableCredentialsList, holderDid, null, null, null, Instant.now().plusSeconds(secondsToAdd)));
                })
                // Log success
                .doOnSuccess(verifiablePresentation -> log.info("Verifiable Presentation created successfully: {}", verifiablePresentation))
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
            }   else {
                    // Throw an exception if the credential list is empty
                    throw new EmptyCredentialListException("Verifiable credentials list is empty");
                }
            } catch (Exception e) {
                throw new IllegalStateException("EError obtaining the subject DID from the verifiable credential" +  e);
        }
    });
    }

}
