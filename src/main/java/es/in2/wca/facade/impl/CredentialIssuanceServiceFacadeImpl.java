package es.in2.wca.facade.impl;

import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.facade.CredentialIssuanceServiceFacade;
import es.in2.wca.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialIssuanceServiceFacadeImpl implements CredentialIssuanceServiceFacade {

    private final CredentialOfferService credentialOfferService;
    private final CredentialIssuerMetadataService credentialIssuerMetadataService;
    private final TokenService tokenService;
    private final CredentialService credentialService;
    private final WalletDataService walletDataService;

    public Mono<CredentialResponse> getCredential(String processId, String authorizationToken, String qrContent) {
        // get Credential Offer
        return credentialOfferService.getCredentialOfferFromCredentialOfferUri(processId, qrContent)
                // get Credential Issuer Metadata
                .flatMap(credentialOffer -> credentialIssuerMetadataService.getCredentialIssuerMetadataFromCredentialOffer(processId, credentialOffer)
                        // get Pre-Authorized Token
                        .flatMap(credentialIssuerMetadata -> tokenService.getPreAuthorizedToken(processId, credentialOffer, credentialIssuerMetadata)
                                // get Credential
                                .flatMap(tokenResponse -> credentialService.getCredential(processId, tokenResponse, credentialIssuerMetadata,authorizationToken)))
                        // save Credential
                        .flatMap(credentialResponse -> walletDataService.saveCredential(processId, authorizationToken, credentialResponse))
                );
    }

}
