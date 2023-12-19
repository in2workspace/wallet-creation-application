package es.in2.wca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import es.in2.wca.configuration.properties.WalletCryptoProperties;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.CredentialResponse;
import es.in2.wca.domain.SignRequest;
import es.in2.wca.domain.TokenResponse;
import es.in2.wca.exception.FailedCommunicationException;
import es.in2.wca.exception.FailedDeserializingException;
import es.in2.wca.exception.ParseErrorException;
import es.in2.wca.service.impl.CredentialServiceImpl;
import es.in2.wca.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.in2.wca.util.Utils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialServiceImplTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WalletCryptoProperties walletCryptoProperties;
    @InjectMocks
    private CredentialServiceImpl credentialService;

    @Test
    void getCredentialTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){
        String processId = "processId";

        TokenResponse tokenResponse = TokenResponse.builder().accessToken("token").cNonce("nonce").build();

        CredentialIssuerMetadata credentialIssuerMetadata = CredentialIssuerMetadata.builder().credentialIssuer("issuer").credentialEndpoint("endpoint").build();

        String authorizationToken = "authToken";

        CredentialResponse mockCredentialResponse = CredentialResponse.builder().credential("credential").c_nonce("fresh_nonce").c_nonce_expires_in(600).format("jwt").build();

        List<Map.Entry<String, String>> headersForCryptoDid = new ArrayList<>();
        headersForCryptoDid.add(new AbstractMap.SimpleEntry<>(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken));

        List<Map.Entry<String, String>> headersForCryptoSign = new ArrayList<>();
        headersForCryptoSign.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));

        List<Map.Entry<String, String>> headersForIssuer = new ArrayList<>();
        headersForIssuer.add(new AbstractMap.SimpleEntry<>(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        headersForIssuer.add(new AbstractMap.SimpleEntry<>(HEADER_AUTHORIZATION, BEARER + tokenResponse.accessToken()));

        String json = "{\"document\":\"sign this document\"}";

        ObjectMapper objectMapper2 = new ObjectMapper();

        JsonNode jsonNode = objectMapper2.readTree(json);

        SignRequest signRequest = SignRequest.builder().did("did:key:123").document(jsonNode).documentType(JWT_PROOF_CLAIM).build();

        when(walletCryptoProperties.url()).thenReturn("cryptoUrl");
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
        when(objectMapper.writeValueAsString(any())).thenReturn("credentialRequest");
        when(objectMapper.readValue(anyString(), eq(CredentialResponse.class))).thenReturn(mockCredentialResponse);
        ObjectWriter mockWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
        when(mockWriter.writeValueAsString(signRequest)).thenReturn("document to sign");

        when(postRequest("cryptoUrl/api/v2/dids/key", headersForCryptoDid, "")).thenReturn(Mono.just("did:key:123"));
        when(postRequest("cryptoUrl/api/v2/sign", headersForCryptoSign, "document to sign")).thenReturn(Mono.just("signed document"));
        when(postRequest(credentialIssuerMetadata.credentialEndpoint(), headersForIssuer, "credentialRequest")).thenReturn(Mono.just("credential"));

        StepVerifier.create(credentialService.getCredential(processId, tokenResponse, credentialIssuerMetadata, authorizationToken))
                .expectNext(mockCredentialResponse)
                .verifyComplete();
        }
    }
    @Test
    void getCredentialCommunicationErrorTest(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)){

            String processId = "processId";

            TokenResponse tokenResponse = TokenResponse.builder().accessToken("token").cNonce("nonce").build();

            CredentialIssuerMetadata credentialIssuerMetadata = CredentialIssuerMetadata.builder().credentialIssuer("issuer").credentialEndpoint("endpoint").build();

            String authorizationToken = "authToken";

            when(postRequest(anyString(), anyList(), anyString()))
                    .thenReturn(Mono.error(new RuntimeException("Communication error")));

            StepVerifier.create(credentialService.getCredential(processId, tokenResponse, credentialIssuerMetadata, authorizationToken))
                    .expectError(FailedCommunicationException.class)
                    .verify();
        }
    }


}
