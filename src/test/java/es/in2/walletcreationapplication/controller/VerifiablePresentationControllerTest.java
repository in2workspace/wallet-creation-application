package es.in2.walletcreationapplication.controller;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import es.in2.walletcreationapplication.domain.VcBasicDataDTO;
//import es.in2.walletcreationapplication.domain.VcSelectorResponseDTO;
//import es.in2.walletcreationapplication.service.SiopService;
//import es.in2.walletcreationapplication.service.VerifiablePresentationService;
//import es.in2.walletcreationapplication.service.WalletDataCommunicationService;
//import id.walt.credentials.w3c.PresentableCredential;
//import id.walt.credentials.w3c.VerifiableCredential;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@WebFluxTest(VerifiablePresentationControllerTest.class)
//class VerifiablePresentationControllerTest {
//    private WebTestClient webTestClient;
//    @MockBean
//    private SiopService siopService;
//    @MockBean
//    private VerifiablePresentationService verifiablePresentationService;
//    @MockBean
//    private WalletDataCommunicationService walletDataCommunicationService;
//
//
//    @BeforeEach
//    void setUp(){
//        webTestClient = WebTestClient.bindToController(new VerifiablePresentationController(verifiablePresentationService,siopService,walletDataCommunicationService))
//                .configureClient()
//                .build();
//    }
//    @Test
//    void testCreateVerifiablePresentation(){
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode credentialSubjectNode = mapper.createObjectNode();
//        credentialSubjectNode.put("firstName", "John");
//        credentialSubjectNode.put("lastName", "Doe");
//        credentialSubjectNode.put("email", "johndoe@example.com");
//        VcBasicDataDTO vcBasicDataDTO = VcBasicDataDTO
//                .builder()
//                .id("4321")
//                .vcType(List.of("LEARCredential", "VerifiableCredential"))
//                .credentialSubject(credentialSubjectNode)
//                .build();
//        VcSelectorResponseDTO vcSelectorResponseDTO = VcSelectorResponseDTO
//                .builder()
//                .redirectUri("exampleUri")
//                .state("1234")
//                .selectedVcList(List.of(vcBasicDataDTO))
//                .build();
//        String token = "ey123";
//        String credentail = "eyJraWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4jelEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpEbmFlZnk3amhwY0ZCanp0TXJFSktFVHdFU0NoUXd4cEpuVUpLb3ZzWUQ1ZkpabXAiLCJuYmYiOjE2OTgxMzQ4NTUsImlzcyI6ImRpZDprZXk6elEzc2h0Y0VRUDN5dXhia1oxU2pONTFUOGZRbVJ5WG5qclhtOEU4NFdMWEtEUWJSbiIsImV4cCI6MTcwMDcyNjg1NSwiaWF0IjoxNjk4MTM0ODU1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiTEVBUkNyZWRlbnRpYWwiXSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL2RvbWUtbWFya2V0cGxhY2UuZXUvLzIwMjIvY3JlZGVudGlhbHMvbGVhcmNyZWRlbnRpYWwvdjEiXSwiaWQiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkiLCJpc3N1ZXIiOnsiaWQiOiJkaWQ6a2V5OnpRM3NodGNFUVAzeXV4YmtaMVNqTjUxVDhmUW1SeVhuanJYbThFODRXTFhLRFFiUm4ifSwiaXNzdWFuY2VEYXRlIjoiMjAyMy0xMC0yNFQwODowNzozNVoiLCJpc3N1ZWQiOiIyMDIzLTEwLTI0VDA4OjA3OjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMTAtMjRUMDg6MDc6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTExLTIzVDA4OjA3OjM1WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6RG5hZWZ5N2pocGNGQmp6dE1yRUpLRVR3RVNDaFF3eHBKblVKS292c1lENWZKWm1wIiwidGl0bGUiOiJNci4iLCJmaXJzdF9uYW1lIjoiSm9obiIsImxhc3RfbmFtZSI6IkRvZSIsImdlbmRlciI6Ik0iLCJwb3N0YWxfYWRkcmVzcyI6IiIsImVtYWlsIjoiam9obmRvZUBnb29kYWlyLmNvbSIsInRlbGVwaG9uZSI6IiIsImZheCI6IiIsIm1vYmlsZV9waG9uZSI6IiszNDc4NzQyNjYyMyIsImxlZ2FsUmVwcmVzZW50YXRpdmUiOnsiY24iOiI1NjU2NTY1NlYgSmVzdXMgUnVpeiIsInNlcmlhbE51bWJlciI6IjU2NTY1NjU2ViIsIm9yZ2FuaXphdGlvbklkZW50aWZpZXIiOiJWQVRFUy0xMjM0NTY3OCIsIm8iOiJHb29kQWlyIiwiYyI6IkVTIn0sInJvbGVzQW5kRHV0aWVzIjpbeyJ0eXBlIjoiTEVBUkNyZWRlbnRpYWwiLCJpZCI6Imh0dHBzOi8vZG9tZS1tYXJrZXRwbGFjZS5ldS8vbGVhci92MS82NDg0OTk0bjRyOWU5OTA0OTQifV0sImtleSI6InZhbHVlIn19LCJqdGkiOiJ1cm46dXVpZDo4NzAwYmVlNS00NjIxLTQ3MjAtOTRkZS1lODY2ZmI3MTk3ZTkifQ.2_YNY515CaohirD4AHDBMvzDagEn-p8uAsaiMT0H4ltK2uVfG8IWWqV_OOR6lFlXMzUhJd7nKsaWkhnAQY8kyA";
//
//        PresentableCredential presentableCredential = new PresentableCredential(VerifiableCredential.Companion.fromString(credentail), null, false);
//        List<PresentableCredential> presentableCredentialList = List.of(presentableCredential);
//        Mockito.when(walletDataCommunicationService.getVerifiableCredentials(vcSelectorResponseDTO,"Bearer "+token))
//                .thenReturn(Mono.just(presentableCredentialList));
//        Mockito.when(verifiablePresentationService.createVerifiablePresentation(presentableCredentialList,token))
//                        .thenReturn(Mono.just("exampleVP"));
//        Mockito.when(siopService.sendAuthenticationResponse(vcSelectorResponseDTO,"example"))
//                .thenReturn(Mono.just("VP Response"));
//
//        webTestClient.post()
//                .uri("/api/vp")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(vcSelectorResponseDTO)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(String.class).isEqualTo("VP Response");
//    }
//}
