package es.in2.wca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.in2.wca.configuration.properties.AuthServerProperties;
import es.in2.wca.domain.CredentialIssuerMetadata;
import es.in2.wca.domain.CredentialOffer;
import es.in2.wca.service.impl.CredentialIssuerMetadataServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialIssuerMetadataServiceImplTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuthServerProperties authServerProperties;
    @InjectMocks
    private CredentialIssuerMetadataServiceImpl credentialIssuerMetadataService;

    @Test
    void getCredentialIssuerMetadataFromCredentialOfferWithCredentialEndpointHardcodedTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)) {
            String processId = "123";
            CredentialOffer credentialOffer = CredentialOffer.builder().credentialIssuer("example").build();
            List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            CredentialIssuerMetadata credentialIssuerMetadataWithoutTheHardcodedEndpoint = CredentialIssuerMetadata.builder().credentialIssuer("example").build();
            CredentialIssuerMetadata expectedCredentialIssuerMetadata = CredentialIssuerMetadata.builder().credentialIssuer("example").authorizationServer("https://example.com").build();

            String json = "{\"credential_token\":\"example\"}";
            ObjectMapper objectMapper2 = new ObjectMapper();
            JsonNode jsonNode = objectMapper2.readTree(json);

            when(authServerProperties.domain()).thenReturn("https://example.com");
            when(getRequest("example/.well-known/openid-credential-issuer",headers)).thenReturn(Mono.just("response"));
            when(objectMapper.readTree("response")).thenReturn(jsonNode);
            when(objectMapper.treeToValue(jsonNode, CredentialIssuerMetadata.class)).thenReturn(credentialIssuerMetadataWithoutTheHardcodedEndpoint);

            StepVerifier.create(credentialIssuerMetadataService.getCredentialIssuerMetadataFromCredentialOffer(processId,credentialOffer))
                    .expectNext(expectedCredentialIssuerMetadata)
                    .verifyComplete();

        }
    }
    @Test
    void getCredentialIssuerMetadataFromCredentialOfferWithoutCredentialEndpointHardcodedTest() throws JsonProcessingException {
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)) {
            String processId = "123";
            CredentialOffer credentialOffer = CredentialOffer.builder().credentialIssuer("example").build();
            List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
            CredentialIssuerMetadata expectedCredentialIssuerMetadata = CredentialIssuerMetadata.builder().credentialIssuer("example").build();

            String json = "{\"credential_endpoint\":\"example\"}";
            ObjectMapper objectMapper2 = new ObjectMapper();
            JsonNode jsonNode = objectMapper2.readTree(json);

            when(getRequest("example/.well-known/openid-credential-issuer",headers)).thenReturn(Mono.just("response"));
            when(objectMapper.readTree("response")).thenReturn(jsonNode);
            when(objectMapper.readValue("response", CredentialIssuerMetadata.class)).thenReturn(expectedCredentialIssuerMetadata);

            StepVerifier.create(credentialIssuerMetadataService.getCredentialIssuerMetadataFromCredentialOffer(processId,credentialOffer))
                    .expectNext(expectedCredentialIssuerMetadata)
                    .verifyComplete();

        }
    }
    @Test
    void getCredentialIssuerMetadataError(){
        try (MockedStatic<Utils> ignored = Mockito.mockStatic(Utils.class)) {
            String processId = "123";
            CredentialOffer credentialOffer = CredentialOffer.builder().credentialIssuer("example").build();
            List<Map.Entry<String, String>> headers = List.of(Map.entry(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));

            when(getRequest("example/.well-known/openid-credential-issuer",headers)).thenReturn(Mono.error(new RuntimeException()));
            StepVerifier.create(credentialIssuerMetadataService.getCredentialIssuerMetadataFromCredentialOffer(processId,credentialOffer))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }


}
