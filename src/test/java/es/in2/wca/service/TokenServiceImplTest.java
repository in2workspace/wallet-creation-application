package es.in2.wca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.domain.AuthorisationServerMetadata;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.domain.TokenResponse;
import es.in2.wca.service.impl.TokenServiceImpl;
import es.in2.wca.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void getPreAuthorizedTokenTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)) {
            String processId = "123";
            CredentialOffer.Grant.PreAuthorizedCodeGrant preAuthorizedCodeGrant = CredentialOffer.Grant.PreAuthorizedCodeGrant.builder().preAuthorizedCode("321").build();
            CredentialOffer.Grant grant = CredentialOffer.Grant.builder().preAuthorizedCodeGrant(preAuthorizedCodeGrant).build();
            CredentialOffer credentialOffer = CredentialOffer.builder().grant(grant).build();
            AuthorisationServerMetadata authorisationServerMetadata = AuthorisationServerMetadata.builder().tokenEndpoint("/token").build();
            TokenResponse expectedTokenResponse = TokenResponse.builder().accessToken("example token").build();
            List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));

            when(postRequest(eq(authorisationServerMetadata.tokenEndpoint()), eq(headers), anyString()))
                    .thenReturn(Mono.just("token response"));
            when(objectMapper.readValue("token response", TokenResponse.class)).thenReturn(expectedTokenResponse);

            StepVerifier.create(tokenService.getPreAuthorizedToken(processId,credentialOffer,authorisationServerMetadata))
                .expectNext(expectedTokenResponse)
                .verifyComplete();
        }
    }
    @Test
    void getPreAuthorizedTokenExceptionTest(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)) {
            String processId = "123";
            CredentialOffer.Grant.PreAuthorizedCodeGrant preAuthorizedCodeGrant = CredentialOffer.Grant.PreAuthorizedCodeGrant.builder().preAuthorizedCode("321").build();
            CredentialOffer.Grant grant = CredentialOffer.Grant.builder().preAuthorizedCodeGrant(preAuthorizedCodeGrant).build();
            CredentialOffer credentialOffer = CredentialOffer.builder().grant(grant).build();
            AuthorisationServerMetadata authorisationServerMetadata = AuthorisationServerMetadata.builder().tokenEndpoint("/token").build();
            List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED_FORM));

            when(postRequest(eq(authorisationServerMetadata.tokenEndpoint()), eq(headers), anyString())).thenReturn(Mono.error(new RuntimeException()));

            StepVerifier.create(tokenService.getPreAuthorizedToken(processId,credentialOffer,authorisationServerMetadata))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

}
