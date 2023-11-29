package es.in2.walletcreationapplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.in2.walletcreationapplication.domain.QrContentDTO;
import es.in2.walletcreationapplication.domain.VcBasicDataDTO;
import es.in2.walletcreationapplication.domain.VcSelectorRequestDTO;
import es.in2.walletcreationapplication.service.SiopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(SiopController.class)
class SiopControllerTest {
    private WebTestClient webTestClient;
    @MockBean
    private SiopService siopService;


    @BeforeEach
    void setUp(){
        webTestClient = WebTestClient.bindToController(new SiopController(siopService))
                .configureClient()
                .build();
    }
    @Test
    void testGetSiopAuthenticationRequest(){
        QrContentDTO qrContentDTO = QrContentDTO.builder()
                .content("example")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode credentialSubjectNode = mapper.createObjectNode();
        credentialSubjectNode.put("firstName", "John");
        credentialSubjectNode.put("lastName", "Doe");
        credentialSubjectNode.put("email", "johndoe@example.com");
        VcBasicDataDTO vcBasicDataDTO = VcBasicDataDTO
                .builder()
                .id("4321")
                .vcType(List.of("LEARCredential", "VerifiableCredential"))
                .credentialSubject(credentialSubjectNode)
                .build();
        VcSelectorRequestDTO vcSelectorRequestDTOResponse = VcSelectorRequestDTO
                .builder()
                .redirectUri("exampleUri")
                .state("1234")
                .selectableVcList(List.of(vcBasicDataDTO))
                .build();
        String token = "ey123";
        Mockito.when(siopService.getSiopAuthenticationRequest(qrContentDTO.getContent(), token))
                .thenReturn(Mono.just(vcSelectorRequestDTOResponse));
        webTestClient.post()
                .uri("/api/siop")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(qrContentDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.redirectUri").isEqualTo("exampleUri")
                .jsonPath("$.state").isEqualTo("1234")
                .jsonPath("$.selectableVcList[0].id").isEqualTo("4321")
                .jsonPath("$.selectableVcList[0].credentialSubject.firstName").isEqualTo("John");
    }
    @Test
    void testProcessSiopAuthenticationRequest(){
        QrContentDTO qrContentDTO = QrContentDTO.builder()
                .content("example")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode credentialSubjectNode = mapper.createObjectNode();
        credentialSubjectNode.put("firstName", "John");
        credentialSubjectNode.put("lastName", "Doe");
        credentialSubjectNode.put("email", "johndoe@example.com");
        VcBasicDataDTO vcBasicDataDTO = VcBasicDataDTO
                .builder()
                .id("4321")
                .vcType(List.of("LEARCredential", "VerifiableCredential"))
                .credentialSubject(credentialSubjectNode)
                .build();
        VcSelectorRequestDTO vcSelectorRequestDTOResponse = VcSelectorRequestDTO
                .builder()
                .redirectUri("exampleUri")
                .state("1234")
                .selectableVcList(List.of(vcBasicDataDTO))
                .build();
        String token = "ey123";
        Mockito.when(siopService.processSiopAuthenticationRequest(qrContentDTO.getContent(), token))
                .thenReturn(Mono.just(vcSelectorRequestDTOResponse));
        webTestClient.post()
                .uri("/api/siop/process")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(qrContentDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.redirectUri").isEqualTo("exampleUri")
                .jsonPath("$.state").isEqualTo("1234")
                .jsonPath("$.selectableVcList[0].id").isEqualTo("4321")
                .jsonPath("$.selectableVcList[0].credentialSubject.firstName").isEqualTo("John");
    }

}
