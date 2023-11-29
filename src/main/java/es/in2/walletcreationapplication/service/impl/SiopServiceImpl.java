package es.in2.walletcreationapplication.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import es.in2.walletcreationapplication.domain.SelectableVCsRequestDTO;
import es.in2.walletcreationapplication.domain.VcBasicDataDTO;
import es.in2.walletcreationapplication.domain.VcSelectorRequestDTO;
import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
import es.in2.walletcreationapplication.exception.FailedDeserializingException;
import es.in2.walletcreationapplication.service.SiopService;
import es.in2.walletcreationapplication.service.TokenVerificationService;
import id.walt.credentials.w3c.VerifiableCredential;
import id.walt.credentials.w3c.VerifiablePresentation;
import id.walt.model.dif.DescriptorMapping;
import id.walt.model.dif.PresentationSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.in2.walletcreationapplication.util.Utils.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiopServiceImpl implements SiopService {
    private final TokenVerificationService tokenVerificationService;
    private final ObjectMapper objectMapper;
    @Value("${app.url.wallet-data}")
    private String urlWalletData;

    @Override
    public Mono<VcSelectorRequestDTO> getSiopAuthenticationRequest(String siopAuthenticationRequestUri, String token) {
        log.info("SiopServiceImpl.getSiopAuthenticationRequest()");

        return getSiopAuthenticationRequestInJwsFormat(siopAuthenticationRequestUri)
                .flatMap(jwtSiopAuthRequest -> {
                    log.debug(jwtSiopAuthRequest);
                    return tokenVerificationService.verifySiopAuthRequestAsJwsFormat(jwtSiopAuthRequest)
                            .then(getAuthRequestClaim(jwtSiopAuthRequest))
                            .flatMap(siopAuthenticationRequest -> processSiopAuthenticationRequest(siopAuthenticationRequest, token));
                });
    }

    @Override
    public Mono<VcSelectorRequestDTO> processSiopAuthenticationRequest(String siopAuthenticationRequest, String token) {
        return parseOpenIdConfig(siopAuthenticationRequest)
                .flatMap(parsedSiopAuthenticationRequest -> extractScopeClaimOfTheSiopAuthRequest(siopAuthenticationRequest)
                        .flatMap(scopeList -> parserSelectableVCToString(SelectableVCsRequestDTO.builder().vcTypes(scopeList).build())
                                .flatMap(selectableVCsRequestDTO -> {
                                    List<Map.Entry<String, String>> headers = new ArrayList<>();
                                    headers.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, "Bearer "+token));
                                    headers.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));

                                    String url = urlWalletData + GET_SELECTABLE_VCS;
                                    return postRequest(url, headers, selectableVCsRequestDTO)
                                            .flatMap(response -> {
                                                try {
                                                    VcBasicDataDTO[] selectableVCsResponseDTO = objectMapper.readValue(response, VcBasicDataDTO[].class);
                                                    VcSelectorRequestDTO vcSelectorRequestDTO = VcSelectorRequestDTO.builder().redirectUri(parsedSiopAuthenticationRequest.getRedirectUri()).state(parsedSiopAuthenticationRequest.getState()).selectableVcList(Arrays.asList(selectableVCsResponseDTO)).build();
                                                    return Mono.just(vcSelectorRequestDTO);
                                                }
                                                catch (Exception e){
                                                    log.error("Error while fetching selectableVCs: " + response, e);
                                                    return Mono.error(new FailedDeserializingException("Error while fetching selectableVCs:: " + e));
                                                }
                                            });
                                })
                        ));
    }

    @Override
    public Mono<String> sendAuthenticationResponse(VcSelectorResponseDTO vcSelectorResponseDTO, String vp) {
        log.info("SiopServiceImpl.sendAuthenticationResponse()");

        return generateDescriptorMap(vp)
                .flatMap(descriptorMap -> {
                    PresentationSubmission presentationSubmission = new PresentationSubmission(
                            Collections.singletonList(descriptorMap),
                            "CustomerPresentationDefinition",
                            "CustomerPresentationSubmission"
                    );

                    return parserPresentationSubmissionToString(presentationSubmission);
                })
                .flatMap(presentationSubmissionString -> {
                    String formData = "state=" + vcSelectorResponseDTO.getState() +
                            "&vp_token=" + vp +
                            "&presentation_submission=" + presentationSubmissionString;

                    log.info("RedirectUri: {}", vcSelectorResponseDTO.getRedirectUri());
                    log.info("FormData: {}", formData);

                    List<Map.Entry<String, String>> headers = List.of(
                            new AbstractMap.SimpleEntry<>(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM)
                    );

                    return postRequest(vcSelectorResponseDTO.getRedirectUri(), headers, formData);
                })
                .doOnNext(response -> log.info("Response body = {}", response));
    }

    private Mono<String> getSiopAuthenticationRequestInJwsFormat(String siopAuthenticationRequestUri) {
        return getRequest(siopAuthenticationRequestUri, Collections.emptyList());
    }

    private Mono<String> getAuthRequestClaim(String jwtSiopAuthRequest) {
        return Mono.fromCallable(() -> {
            JWSObject jwsObject = JWSObject.parse(jwtSiopAuthRequest);
            return jwsObject.getPayload().toJSONObject().get("auth_request").toString();
        });
    }
    private Mono<List<String>> extractScopeClaimOfTheSiopAuthRequest(String siopAuthenticationRequest) {
        return Mono.fromCallable(() -> {
            Pattern scopeRegex = Pattern.compile("scope=\\[([^]]+)]");
            Matcher scopeMatcher = scopeRegex.matcher(siopAuthenticationRequest);

            if (scopeMatcher.find()) {
                String scopeMatch = scopeMatcher.group(1);
                return Arrays.asList(scopeMatch.split(","));
            } else {
                return List.of("VerifiableId");
            }
        });
    }
    private Mono<String> parserSelectableVCToString(SelectableVCsRequestDTO selectableVCsRequestDTO) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(selectableVCsRequestDTO));
    }
    /**
     * Adds a DescriptorMapping to the given DescriptorMapping recursively.
     * If the pathNested of the provided DescriptorMapping is null, it sets the temporary DescriptorMapping as the new pathNested.
     * Otherwise, it recursively calls itself to update the pathNested.
     *
     * @param credentialDescriptorMap The original DescriptorMapping to update.
     * @param tmpCredentialDescriptorMap The DescriptorMapping to add as pathNested.
     * @return A Mono that emits the updated DescriptorMapping.
     */
    public Mono<DescriptorMapping> addCredentialDescriptorMap(DescriptorMapping credentialDescriptorMap, DescriptorMapping tmpCredentialDescriptorMap) {
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

    private Mono<String> parserPresentationSubmissionToString(PresentationSubmission presentationSubmission) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(presentationSubmission))
                .onErrorResume(e -> {
                    // Log and handle the error as needed
                    log.error("Error parsing PresentationSubmission to String", e);
                    return Mono.error(new RuntimeException("Error parsing PresentationSubmission", e));
                });
    }
    private Mono<DescriptorMapping> generateDescriptorMap(String vp) {
        // Parse the Verifiable Presentation
        VerifiablePresentation verifiablePresentation = VerifiablePresentation.Companion.fromString(vp);

        // Process each Verifiable Credential in the Verifiable Presentation
        return Flux.fromIterable(Objects.requireNonNull(verifiablePresentation.getVerifiableCredential()))
                .index()
                .map(indexed -> {
                    VerifiableCredential credential = indexed.getT2();
                    Long index = indexed.getT1();
                    return new DescriptorMapping(
                            JWT_VC,
                            "$.verifiableCredential[" + index + "]",
                            credential.getId(),
                            null  // Assuming null is acceptable for the pathNested initially
                    );
                })
                .collectList()  // Collect DescriptorMappings into a List
                .flatMap(list -> buildDescriptorMapping(list, verifiablePresentation.getId())); // Build the final DescriptorMapping
    }
    private Mono<DescriptorMapping> buildDescriptorMapping(List<DescriptorMapping> list, String vpId) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        }

        Mono<DescriptorMapping> result = Mono.just(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            DescriptorMapping current = list.get(i);
            result = result.flatMap(r -> addCredentialDescriptorMap(r, current));
        }

        return result.map(finalMap -> new DescriptorMapping(
                JWT_VP,
                "$",
                vpId,  // Using the passed VP ID here
                finalMap  // Final combined DescriptorMapping
        ));
    }

}
