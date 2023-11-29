package es.in2.walletcreationapplication.controller;

import es.in2.walletcreationapplication.domain.QrContentDTO;
import es.in2.walletcreationapplication.service.IssuanceFacadeService;
import es.in2.walletcreationapplication.service.VerifiableCredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(VerifiableCredentialControllerTest.class)
class VerifiableCredentialControllerTest {
    private WebTestClient webTestClient;
    @MockBean
    private VerifiableCredentialService verifiableCredentialService;
    @MockBean
    private IssuanceFacadeService issuanceFacadeService;

    @BeforeEach
    void setUp(){
        webTestClient = WebTestClient.bindToController(new VerifiableCredentialController(verifiableCredentialService,issuanceFacadeService))
                .configureClient()
                .build();
    }
    @Test
    void testGetCredentialIssuerMetadata(){
        QrContentDTO qrContentDTO = QrContentDTO.builder()
                .content("example")
                .build();
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE3MDExNzMxMjgsImlhdCI6MTcwMTE3MjgyOCwianRpIjoiYWIxOTJiYzQtNTdlYS00NzdjLWI3MDctNDAzMzcwOGNlZDMxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDdmNjA5YTEtYTNjNS00Y2RkLWE5ZGYtMDgwMGVjY2Y4OGJmIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiI2MzBkYzg0Ny0wYWU5LTRlY2YtOGJiMy02YzJiMTQ3MzBjODYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiNjMwZGM4NDctMGFlOS00ZWNmLThiYjMtNmMyYjE0NzMwYzg2IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqb3NlIiwiZW1haWwiOiJqb3NlQGV4YW1wbGUuY29tIn0.rnTccY3ZtdAODub4C5HobNO3nzcRTChbxs47uay7Euh2qvKe08KYw7j6pT3q4P7bKbyT7Jv1f75YTq9X6uFAGw-nPi_3ve0Ug12WTFumkKeQxWg5I4vLuqjU8mqP0GEog6y4UYqPII8cvC3s1OM6BiPI3B-VrBZy9ttA4a1rALgePk6hEbShN_cJZ3pGi3YS7Lx_i_MH7pOt-5u46zOz3ziQdQztyat0KQAHGvQnS4zUtTSXIKaIFkSiJwjPsSdJAUAqTT247QnNem9Bw108OLDBhMjtMO9L1qPe_RsmNA379g-7eQ27nVMrhytrOsuwKj7-HfMHpDiaKTAPGrv7Zw";
        Mockito.when(verifiableCredentialService.getCredentialIssuerMetadata(qrContentDTO.getContent(),"d7f609a1-a3c5-4cdd-a9df-0800eccf88bf"))
                .thenReturn(Mono.empty());
        webTestClient.post()
                .uri("/api/credentials/issuer-metadata")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(qrContentDTO)
                .exchange()
                .expectStatus().isCreated();
    }
    @Test
    void testGetVC(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE3MDExNzMxMjgsImlhdCI6MTcwMTE3MjgyOCwianRpIjoiYWIxOTJiYzQtNTdlYS00NzdjLWI3MDctNDAzMzcwOGNlZDMxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDdmNjA5YTEtYTNjNS00Y2RkLWE5ZGYtMDgwMGVjY2Y4OGJmIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiI2MzBkYzg0Ny0wYWU5LTRlY2YtOGJiMy02YzJiMTQ3MzBjODYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiNjMwZGM4NDctMGFlOS00ZWNmLThiYjMtNmMyYjE0NzMwYzg2IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqb3NlIiwiZW1haWwiOiJqb3NlQGV4YW1wbGUuY29tIn0.rnTccY3ZtdAODub4C5HobNO3nzcRTChbxs47uay7Euh2qvKe08KYw7j6pT3q4P7bKbyT7Jv1f75YTq9X6uFAGw-nPi_3ve0Ug12WTFumkKeQxWg5I4vLuqjU8mqP0GEog6y4UYqPII8cvC3s1OM6BiPI3B-VrBZy9ttA4a1rALgePk6hEbShN_cJZ3pGi3YS7Lx_i_MH7pOt-5u46zOz3ziQdQztyat0KQAHGvQnS4zUtTSXIKaIFkSiJwjPsSdJAUAqTT247QnNem9Bw108OLDBhMjtMO9L1qPe_RsmNA379g-7eQ27nVMrhytrOsuwKj7-HfMHpDiaKTAPGrv7Zw";
        Mockito.when(issuanceFacadeService.getAndSaveVC(token))
                .thenReturn(Mono.empty());
        webTestClient.post()
                .uri("/api/credentials")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isCreated();
    }

}
