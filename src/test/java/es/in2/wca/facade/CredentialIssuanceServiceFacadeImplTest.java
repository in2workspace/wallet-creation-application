package es.in2.wca.facade;

import es.in2.wca.domain.*;
import es.in2.wca.facade.impl.CredentialIssuanceServiceFacadeImpl;
import es.in2.wca.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialIssuanceServiceFacadeImplTest {
    @Mock
    private CredentialOfferService credentialOfferService;
    @Mock
    private CredentialIssuerMetadataService credentialIssuerMetadataService;
    @Mock
    private AuthorisationServerMetadataService authorisationServerMetadataService;
    @Mock
    private TokenService tokenService;
    @Mock
    private CredentialService credentialService;
    @Mock
    private WalletDataService walletDataService;
    @InjectMocks
    private CredentialIssuanceServiceFacadeImpl credentialIssuanceServiceFacade;

    @Test
    void getCredentialTest(){
        String processId = "123";
        String qrContent = "example content";
        String token = "token example";

        CredentialOffer credentialOffer = CredentialOffer.builder().credentialIssuer("example").build();
        CredentialIssuerMetadata credentialIssuerMetadata = CredentialIssuerMetadata.builder().credentialIssuer("example").build();
        AuthorisationServerMetadata authorisationServerMetadata = AuthorisationServerMetadata.builder().issuer("example").build();
        TokenResponse tokenResponse = TokenResponse.builder().expiresIn(300).build();
        CredentialResponse credentialResponse = CredentialResponse.builder().credential("example credential").build();


        when(credentialOfferService.getCredentialOfferFromCredentialOfferUri(processId,qrContent)).thenReturn(Mono.just(credentialOffer));
        when(credentialIssuerMetadataService.getCredentialIssuerMetadataFromCredentialOffer(processId,credentialOffer)).thenReturn(Mono.just(credentialIssuerMetadata));
        when(authorisationServerMetadataService.getAuthorizationServerMetadataFromCredentialIssuerMetadata(processId,credentialIssuerMetadata)).thenReturn(Mono.just(authorisationServerMetadata));
        when(tokenService.getPreAuthorizedToken(processId,credentialOffer,authorisationServerMetadata)).thenReturn(Mono.just(tokenResponse));
        when(credentialService.getCredential(processId,tokenResponse,credentialIssuerMetadata,token)).thenReturn(Mono.just(credentialResponse));
        when(walletDataService.saveCredential(processId,token,credentialResponse)).thenReturn(Mono.empty());

        StepVerifier.create(credentialIssuanceServiceFacade.getCredential(processId,token,qrContent))
                .verifyComplete();

    }


}
