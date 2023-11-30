package es.in2.wca.facade.impl;

import es.in2.wca.domain.AuthorizationRequest;
import es.in2.wca.domain.CredentialsBasicInfo;
import es.in2.wca.domain.VcSelectorRequest;
import es.in2.wca.domain.VcSelectorResponse;
import es.in2.wca.facade.AttestationExchangeServiceFacade;
import es.in2.wca.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttestationExchangeServiceFacadeImpl implements AttestationExchangeServiceFacade {

    private final AuthorizationRequestService authorizationRequestService;
    private final AuthorizationResponseService authorizationResponseService;
    private final VerifierValidationService verifierValidationService;
    private final WalletDataService walletDataService;
    private final PresentationService presentationService;

    @Override
    public Mono<VcSelectorRequest> getSelectableCredentialsRequiredToBuildThePresentation(String processId, String authorizationToken, String qrContent) {
        log.info("ProcessID: {} - Processing a Verifiable Credential Login Request", processId);
        // Get Authorization Request executing the VC Login Request
        return authorizationRequestService.getAuthorizationRequestFromVcLoginRequest(processId, qrContent, authorizationToken)
                // Validate the Verifier which issues the Authorization Request
                .flatMap(jwtAuthorizationRequest ->
                        verifierValidationService.verifyIssuerOfTheAuthorizationRequest(processId, jwtAuthorizationRequest)
                                // Get the Authorization Request from the JWT Authorization Request Claim
                                .then(authorizationRequestService.getAuthorizationRequestFromJwtAuthorizationRequestClaim(processId, jwtAuthorizationRequest))
                )// Check which Verifiable Credentials are selectable
                .flatMap(authorizationRequest ->
                        walletDataService.getSelectableVCsByAuthorizationRequestScope(processId, authorizationToken, authorizationRequest)
                                // Build the SelectableVCsRequest
                                .flatMap(selectableVCs -> buildSelectableVCsRequest(authorizationRequest, selectableVCs))
                );
    }

    private Mono<VcSelectorRequest> buildSelectableVCsRequest(AuthorizationRequest authorizationRequest, List<CredentialsBasicInfo> selectableVCs) {
        return Mono.fromCallable(() -> VcSelectorRequest.builder()
                .redirectUri(authorizationRequest.redirectUri())
                .state(authorizationRequest.state())
                .selectableVcList(selectableVCs)
                .build());
    }

    @Override
    public Mono<Void> buildVerifiablePresentationWithSelectedVCs(String processId, String authorizationToken, VcSelectorResponse vcSelectorResponse) {
        // Get the Verifiable Credentials which will be used for the Presentation from the Wallet Data Service
        return walletDataService.getVerifiableCredentials(processId, authorizationToken, vcSelectorResponse)
                // Create the Verifiable Presentation
                .flatMap(presentableCredentialsList -> presentationService.createVerifiablePresentation(processId, authorizationToken, presentableCredentialsList))
                // Build the Authentication Response
                // todo: refactor to separate build and post
                // Send the Authentication Response to the Verifier
                .flatMap(verifiablePresentation ->
                        authorizationResponseService.buildAndPostAuthorizationResponseWithVerifiablePresentation(processId, vcSelectorResponse, verifiablePresentation))
                .then();
    }

}
