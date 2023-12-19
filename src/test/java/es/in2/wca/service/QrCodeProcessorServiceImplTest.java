package es.in2.wca.service;

import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.VcSelectorRequest;
import es.in2.wca.exception.NoSuchQrContentException;
import es.in2.wca.facade.AttestationExchangeServiceFacade;
import es.in2.wca.facade.CredentialIssuanceServiceFacade;
import es.in2.wca.service.impl.QrCodeProcessorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrCodeProcessorServiceImplTest {
    @Mock
    private CredentialIssuanceServiceFacade credentialIssuanceServiceFacade;
    @Mock
    private AttestationExchangeServiceFacade attestationExchangeServiceFacade;

    @InjectMocks
    private QrCodeProcessorServiceImpl qrCodeProcessorService;

    @Test
    void processQrContentCredentialOfferUriSuccess() {
        String qrContent = "https://credential-offer";
        String processId = "processId";
        String authorizationToken = "authToken";
        CredentialResponse credentialResponse = CredentialResponse.builder().credential("credential").c_nonce("fresh_nonce").c_nonce_expires_in(600).format("jwt").build();

        when(credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)).thenReturn(Mono.just(credentialResponse));

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectNext(credentialResponse)
                .verifyComplete();
    }
    @Test
    void processQrContentCredentialOfferUriFailure() {
        String qrContent = "https://credential-offer";
        String processId = "processId";
        String authorizationToken = "authToken";

        when(credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)).thenThrow(new RuntimeException());

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void processQrContentOpenIdCredentialOffer() {
        String qrContent = "openid-credential-offer://";
        String processId = "processId";
        String authorizationToken = "authToken";
        CredentialResponse credentialResponse = CredentialResponse.builder().credential("credential").c_nonce("fresh_nonce").c_nonce_expires_in(600).format("jwt").build();

        when(credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)).thenReturn(Mono.just(credentialResponse));

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectNext(credentialResponse)
                .verifyComplete();
    }
    @Test
    void processQrContentOpenIdCredentialOfferFailure() {
        String qrContent = "openid-credential-offer://";
        String processId = "processId";
        String authorizationToken = "authToken";

        when(credentialIssuanceServiceFacade.getCredential(processId, authorizationToken, qrContent)).thenThrow(new RuntimeException());

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectError(RuntimeException.class)
                .verify();
    }
    @Test
    void processQrContentVcLoginRequest() {
        String qrContent = "https://authentication-request";
        String processId = "processId";
        String authorizationToken = "authToken";
        VcSelectorRequest vcSelectorRequest = VcSelectorRequest.builder().redirectUri("uri").state("state").selectableVcList(List.of()).build();
        when(attestationExchangeServiceFacade.getSelectableCredentialsRequiredToBuildThePresentation(processId, authorizationToken, qrContent)).thenReturn(Mono.just(vcSelectorRequest));

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectNext(vcSelectorRequest)
                .verifyComplete();
    }

    @Test
    void processQrContentVcLoginRequestFailure() {
        String qrContent = "https://authentication-request";
        String processId = "processId";
        String authorizationToken = "authToken";

        when(attestationExchangeServiceFacade.getSelectableCredentialsRequiredToBuildThePresentation(processId, authorizationToken, qrContent)).thenThrow(new RuntimeException());

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectError(RuntimeException.class)
                .verify();
    }
    @Test
    void processQrContentOpenIdAuthenticationRequestFailure() {
        String qrContent = "openid://";
        String processId = "processId";
        String authorizationToken = "authToken";

        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectError(NoSuchQrContentException.class)
                .verify();
    }

    @Test
    void processQrContentUnknown() {
        String qrContent = "unknownContent";
        String processId = "processId";
        String authorizationToken = "authToken";
        String expectedErrorMessage = "The received QR content cannot be processed";
        
        StepVerifier.create(qrCodeProcessorService.processQrContent(processId, authorizationToken, qrContent))
                .expectErrorMatches(throwable -> throwable instanceof NoSuchQrContentException &&
                        expectedErrorMessage.equals(throwable.getMessage()))
                .verify();
    }

}
