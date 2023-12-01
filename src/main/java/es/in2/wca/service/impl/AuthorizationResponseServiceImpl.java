package es.in2.wca.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.domain.VcSelectorResponse;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.service.AuthorizationResponseService;
import id.walt.credentials.w3c.VerifiableCredential;
import id.walt.credentials.w3c.VerifiablePresentation;
import id.walt.model.dif.DescriptorMapping;
import id.walt.model.dif.PresentationSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static es.in2.wca.util.Utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationResponseServiceImpl implements AuthorizationResponseService {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> buildAndPostAuthorizationResponseWithVerifiablePresentation(String processId, VcSelectorResponse vcSelectorResponse, String verifiablePresentation) {
        return generateDescriptorMapping(verifiablePresentation)
                .flatMap(descriptorMapping -> getPresentationSubmissionAsString(processId, descriptorMapping))
                .flatMap(presentationSubmissionString -> postAuthorizationResponse(processId, vcSelectorResponse, verifiablePresentation, presentationSubmissionString));
    }

    private Mono<DescriptorMapping> generateDescriptorMapping(String verifiablePresentationString) {
        // Parse the Verifiable Presentation
        VerifiablePresentation verifiablePresentation = VerifiablePresentation.Companion.fromString(verifiablePresentationString);
        // Process each Verifiable Credential in the Verifiable Presentation
        return Flux.fromIterable(Objects.requireNonNull(verifiablePresentation.getVerifiableCredential()))
                .index()
                .map(indexed -> {
                    VerifiableCredential credential = indexed.getT2();
                    Long index = indexed.getT1();
                    // Assuming null is acceptable for the pathNested initially
                    return new DescriptorMapping(JWT_VC, "$.verifiableCredential[" + index + "]", credential.getId(), null);
                })
                .collectList()  // Collect DescriptorMappings into a List
                .flatMap(list -> buildDescriptorMapping(list, verifiablePresentation.getId())); // Build the final DescriptorMapping
    }

    private Mono<DescriptorMapping> buildDescriptorMapping(List<DescriptorMapping> descriptorMappingList, String verifiablePresentationId) {
        // Check if the list is empty
        if (descriptorMappingList == null || descriptorMappingList.isEmpty()) {
            return Mono.empty();
        }
        // If the list has only one element, just return it
        Mono<DescriptorMapping> result = Mono.just(descriptorMappingList.get(0));
        // If the list has more than one element, recursively add the DescriptorMappings
        for (int i = 1; i < descriptorMappingList.size(); i++) {
            DescriptorMapping tmpCredentialDescriptorMap = descriptorMappingList.get(i);
            result = result.flatMap(credentialDescriptorMap ->
                    addCredentialDescriptorMap(credentialDescriptorMap, tmpCredentialDescriptorMap));
        }
        return result.map(finalMap -> new DescriptorMapping(JWT_VP, "$", verifiablePresentationId, finalMap));
    }

    private Mono<String> getPresentationSubmissionAsString(String processId, DescriptorMapping descriptorMapping) {
        return Mono.fromCallable(() -> {
                    // fixme: id and definition_id are hardcoded, need to be dynamic
                    PresentationSubmission presentationSubmission = new PresentationSubmission(
                            Collections.singletonList(descriptorMapping),
                            "CustomerPresentationDefinition",
                            "CustomerPresentationSubmission"
                    );
                    return objectMapper.writeValueAsString(presentationSubmission);
                })
                .doOnSuccess(presentationSubmissionString ->
                        log.info("ProcessID: {} - PresentationSubmission: {}", processId, presentationSubmissionString))
                .onErrorResume(e -> {
                    log.error("ProcessID: {} - Error parsing PresentationSubmission to String: {}", processId, e.getMessage());
                    return Mono.error(new RuntimeException("Error parsing PresentationSubmission", e));
                });
    }

    private Mono<DescriptorMapping> addCredentialDescriptorMap(DescriptorMapping credentialDescriptorMap, DescriptorMapping tmpCredentialDescriptorMap) {
        // If the original DescriptorMapping is null, just return the temporary one
        if (credentialDescriptorMap == null) {
            return Mono.just(tmpCredentialDescriptorMap);
        }
        // If the pathNested of the original DescriptorMapping is null, create a new instance with the updated pathNested
        if (credentialDescriptorMap.getPath_nested() == null) {
            DescriptorMapping updatedMap = new DescriptorMapping(
                    credentialDescriptorMap.getFormat(),
                    credentialDescriptorMap.getPath(),
                    credentialDescriptorMap.getId(),
                    tmpCredentialDescriptorMap
            );
            return Mono.just(updatedMap);
        } else {
            // If pathNested is not null, recursively update pathNested
            return addCredentialDescriptorMap(credentialDescriptorMap.getPath_nested(), tmpCredentialDescriptorMap)
                    .map(updatedNestedMap -> new DescriptorMapping(
                            credentialDescriptorMap.getFormat(),
                            credentialDescriptorMap.getPath(),
                            credentialDescriptorMap.getId(),
                            updatedNestedMap
                    ));
        }
    }

    private Mono<String> postAuthorizationResponse(String processId, VcSelectorResponse vcSelectorResponse,
                                                   String verifiablePresentation, String presentationSubmissionString) {
        // Headers
        List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));
        // Build URL encoded form data request body
        Map<String, String> formDataMap = Map.of(
                "state", vcSelectorResponse.state(),
                "vp_token", verifiablePresentation,
                "presentation_submission", presentationSubmissionString);
        // Build the request body
        String xWwwFormUrlencodedBody = formDataMap.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        // Post request
        return postRequest(vcSelectorResponse.redirectUri(), headers, xWwwFormUrlencodedBody)
                .doOnSuccess(response -> log.info("ProcessID: {} - Authorization Response Response: {}", processId, response))
                .onErrorResume(e -> Mono.error(new FailedCommunicationException("Error while fetching Credential Issuer Metadata from the Issuer")));
    }

}
