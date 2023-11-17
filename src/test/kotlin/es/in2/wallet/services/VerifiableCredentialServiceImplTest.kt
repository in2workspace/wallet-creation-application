package es.in2.wallet.services

import es.in2.wallet.wca.service.CredentialRequestDataService
import es.in2.wallet.wca.service.IssuerService
import es.in2.wallet.wca.service.impl.VerifiableCredentialServiceImpl
import es.in2.wallet.wca.util.ApplicationUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class VerifiableCredentialServiceImplTest {


    private val applicationUtils: ApplicationUtils = mockk(relaxed = true)

    @Mock
    private lateinit var issuerDataService: IssuerService

    @Mock
    private lateinit var credentialRequestDataService: CredentialRequestDataService

    private lateinit var verifiablePresentationServiceImplTest: VerifiableCredentialServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        verifiablePresentationServiceImplTest = VerifiableCredentialServiceImpl("walletDataBaseUrl", "walletCryptoBaseUrl", issuerDataService, credentialRequestDataService, applicationUtils)
    }

    @Test
    fun testGetCredentialIssuerMetadata() {
        // Prepare test data
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxOGFyVmZaZTJpQkJoaU56RURnT3c3Tlc1ZmZHNElLTEtOSmVIOFQxdjJNIn0.eyJleHAiOjE2OTk0NDI5MzMsImlhdCI6MTY5OTQ0MjYzMywianRpIjoiZTg1ODRiZGMtNGZjMS00OWU0LWIyNmItZmEzNTBmNGM4NmNmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg0L3JlYWxtcy9XYWxsZXRJZFAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOWY5Y2MyZGEtOWM3My00YTkwLWJmYzMtOWM3Y2VmM2UwZjA1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2FsbGV0LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJkN2MyMjNhNi1lMTQ4LTRjNzUtODlhZi0zMzI3MjEzNjcyMGQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy13YWxsZXRpZHAiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiZDdjMjIzYTYtZTE0OC00Yzc1LTg5YWYtMzMyNzIxMzY3MjBkIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MDQiLCJlbWFpbCI6InRlc3QwNEBnbWFpbC5jb20ifQ.t7EYK93ENT-5v2i5KUaSGAzOLaa1OxV6cCLSJ6aQKB20WAfAxmYLJewWs9mmdAEwqWe4bwkbdpFAPyalIZVySc6PuVV4DWy0ge1C_PzRCeS2MQ5jU_KYBfTIaZHE5PUrUs75-MChHP2IcTGc3Tl2cJ3WaojWkWg--sIobDokhq9_okfejIE7M-QDDWHVSlljZnq0nOsXuCNIdcQ6QXHikoR_DpS9AmW-orWFMeu1ENjk7Dc1c3UfVTAIs1dB84QldYSvWm1ezPnFInd_fLaw6hvfqXCvabbbHZu6JFijf_hf5JqhKoLXpD3t4gB2EP9U7ZpJUfoG_2JqTLLwNgbMyg"
        val credentialOfferUriExtended = "http://issuer-api:8081/credential-offer?credential_offer_uri=http://issuer-api:8081/credential-offer/KFAbGOl0ScmsT_jQt1Pyyw"

        val credentialOfferForPreAuthorizedCodeFlow =
            """
            {
                "credential_issuer":"http://issuer-api:8081",
                "credentials":["LEARCredential"],
                "grants":
                    {
                    "urn:ietf:params:oauth:grant-type:pre-authorized_code":
                        {
                        "pre-authorized_code":"8a159447-e5c7-48e5-a9b5-a4c218a4ef8b.fce54742-a009-4f08-a443-57b9d3a1811c.7049b007-64b2-407f-938a-a97de616d086",
                        "user_pin_required":false
                        }
                    }
            }
            """

        every {
            applicationUtils.getRequest("http://issuer-api:8081/credential-offer/KFAbGOl0ScmsT_jQt1Pyyw", any())
        } returns credentialOfferForPreAuthorizedCodeFlow


        val credentialIssuerMetadata =
            """
            {
               "credential_issuer":"http://issuer-api:8081",
               "credential_endpoint":"http://issuer-api:8081/api/vc/type/",
               "credential_token":"http://issuer-keycloak:8080/realms/EAAProvider/verifiable-credential/did:key:z6MkqmaCT2JqdUtLeKah7tEVfNXtDXtQyj4yxEgV11Y5CqUa/token",
               "credentials_supported":[
                  {
                     "format":"jwt_vc_json",
                     "id":"VerifiableId_JWT",
                     "types":[
                        "VerifiableCredential",
                        "VerifiableAttestation",
                        "VerifiableId"
                     ],
                     "cryptographic_binding_methods_supported":[
                        "did"
                     ],
                     "cryptographic_suites_supported":[
                        
                     ],
                     "credentialSubject":{
                        "name":"VerifiableId",
                        "template":{
                           "type":[
                              "VerifiableCredential",
                              "VerifiableAttestation",
                              "VerifiableId"
                           ],
                           "context":[
                              {
                                 "uri":"https://www.w3.org/2018/credentials/v1",
                                 "_isObject":false,
                                 "properties":{
                                    
                                 }
                              }
                           ],
                           "id":"urn:uuid:3add94f4-28ec-42a1-8704-4e4aa51006b4",
                           "issuer":{
                              "id":"did:ebsi:2A9BZ9SUe6BatacSpvs1V5CdjHvLpQ7bEsi2Jb6LdHKnQxaN",
                              "_isObject":false,
                              "properties":{
                                 
                              }
                           },
                           "issuanceDate":"2021-08-31T00:00:00Z",
                           "issued":"2021-08-31T00:00:00Z",
                           "validFrom":"2021-08-31T00:00:00Z",
                           "expirationDate":null,
                           "proof":null,
                           "credentialSchema":{
                              "id":"https://raw.githubusercontent.com/walt-id/waltid-ssikit-vclib/master/src/test/resources/schemas/VerifiableId.json",
                              "type":"FullJsonSchemaValidator2021",
                              "properties":{
                                 "id":"https://raw.githubusercontent.com/walt-id/waltid-ssikit-vclib/master/src/test/resources/schemas/VerifiableId.json"
                              }
                           },
                           "credentialSubject":{
                              "id":"did:ebsi:2AEMAqXWKYMu1JHPAgGcga4dxu7ThgfgN95VyJBJGZbSJUtp",
                              "properties":{
                                 "currentAddress":[
                                    "1 Boulevard de la Liberté, 59800 Lille"
                                 ],
                                 "dateOfBirth":"1993-04-08",
                                 "familyName":"DOE",
                                 "firstName":"Jane",
                                 "gender":"FEMALE",
                                 "nameAndFamilyNameAtBirth":"Jane DOE",
                                 "personalIdentifier":"0904008084H",
                                 "placeOfBirth":"LILLE, FRANCE"
                              }
                           },
                           "properties":{
                              "evidence":[
                                 {
                                    "documentPresence":[
                                       "Physical"
                                    ],
                                    "evidenceDocument":[
                                       "Passport"
                                    ],
                                    "subjectPresence":"Physical",
                                    "type":[
                                       "DocumentVerification"
                                    ],
                                    "verifier":"did:ebsi:2A9BZ9SUe6BatacSpvs1V5CdjHvLpQ7bEsi2Jb6LdHKnQxaN"
                                 }
                              ]
                           },
                           "sdJwt":null,
                           "selectiveDisclosure":null,
                           "issuerId":"did:ebsi:2A9BZ9SUe6BatacSpvs1V5CdjHvLpQ7bEsi2Jb6LdHKnQxaN",
                           "subjectId":"did:ebsi:2AEMAqXWKYMu1JHPAgGcga4dxu7ThgfgN95VyJBJGZbSJUtp",
                           "challenge":null
                        },
                        "mutable":false
                     }
                  },
                  {
                     "format":"jwt_vc_json",
                     "id":"LEARCredential",
                     "types":[
                        "VerifiableCredential",
                        "VerifiableAttestation",
                        "LEARCredential"
                     ],
                     "cryptographic_binding_methods_supported":[
                        "did"
                     ],
                     "cryptographic_suites_supported":[
                        
                     ],
                     "credentialSubject":{
                        "name":"LEARCredential",
                        "template":{
                           "type":[
                              "VerifiableCredential",
                              "LEARCredential"
                           ],
                           "context":[
                              {
                                 "uri":"https://www.w3.org/2018/credentials/v1",
                                 "_isObject":false,
                                 "properties":{
                                    
                                 }
                              },
                              {
                                 "uri":"https://dome-marketplace.eu//2022/credentials/learcredential/v1",
                                 "_isObject":false,
                                 "properties":{
                                    
                                 }
                              }
                           ],
                           "id":null,
                           "issuer":{
                              "id":"",
                              "_isObject":true,
                              "properties":{
                                 
                              }
                           },
                           "issuanceDate":"2022-03-22T14:00:00Z",
                           "issued":null,
                           "validFrom":"2022-03-22T14:00:00Z",
                           "expirationDate":"2023-03-22T14:00:00Z",
                           "proof":null,
                           "credentialSchema":null,
                           "credentialSubject":{
                              "id":null,
                              "properties":{
                                 "title":"Mr.",
                                 "first_name":"John",
                                 "last_name":"Doe",
                                 "gender":"M",
                                 "postal_address":"",
                                 "email":"johndoe@goodair.com",
                                 "telephone":"",
                                 "fax":"",
                                 "mobile_phone":"+34787426623",
                                 "legalRepresentative":{
                                    "cn":"56565656V Jesus Ruiz",
                                    "serialNumber":"56565656V",
                                    "organizationIdentifier":"VATES-12345678",
                                    "o":"GoodAir",
                                    "c":"ES"
                                 },
                                 "rolesAndDuties":[
                                    {
                                       "type":"LEARCredential",
                                       "id":"https://dome-marketplace.eu//lear/v1/6484994n4r9e990494"
                                    }
                                 ]
                              }
                           },
                           "properties":{
                              
                           },
                           "sdJwt":null,
                           "selectiveDisclosure":null,
                           "issuerId":"",
                           "subjectId":null,
                           "challenge":null
                        },
                        "mutable":true
                     }
                  }
               ]
            }
            """

        every {
            applicationUtils.getRequest("http://issuer-api:8081/.well-known/openid-credential-issuer", any())
        } returns credentialIssuerMetadata

        Mockito.doNothing().`when`(issuerDataService).upsertIssuerData(anyString(), anyString())

        val accessTokenAndNonceJson =
            """
            {
                "access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJRcXlubjlWcHFyU2F3Wk5yTWp3Sk1jVHNtRGI5R1JVRUNXVGNoS2lIWi1NIn0.eyJleHAiOjE2OTk1Mzk2NzUsImlhdCI6MTY5OTUzOTM3NSwianRpIjoiZDQ1ZTUwOWQtMzQ4Yy00Y2M2LWJjNzktMzMwZGUzYjJkNDIzIiwiaXNzIjoiaHR0cDovL2lzc3Vlci1rZXljbG9hazo4MDgwL3JlYWxtcy9FQUFQcm92aWRlciIsImF1ZCI6InJlYWxtLW1hbmFnZW1lbnQiLCJzdWIiOiIyZWI2NWY0ZC03YzE2LTRiMjUtYjNlZi04NDRlMjg3MmIyMDMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvaWRjNHZjaS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZmNlNTQ3NDItYTAwOS00ZjA4LWE0NDMtNTdiOWQzYTE4MTFjIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjQyMDEiLCJodHRwczovL2lzc3VlcmRldi5pbjIuZXMvKiIsImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwidmlldy1hdXRob3JpemF0aW9uIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiLCJxdWVyeS11c2VycyJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImZjZTU0NzQyLWEwMDktNGYwOC1hNDQzLTU3YjlkM2ExODExYyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlN1cGVyIEFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiaW4yYWRtaW4xIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIiwiZmFtaWx5X25hbWUiOiJBZG1pbiIsImVtYWlsIjoiYWRtaW51c2VyQGV4YW1wbGUuY29tIn0.jvNW9lsKPHZe-STBmyALNDW3zPQN5pmzOTxaJfH-FjOCTXgXIn5TjJX6Y604DYt6DyXTvOHTWj62sNXjVNy8GhyzPrVsWPnHKPLtYCyXXr7X8nwJHaopAbaKsljxIUgYClwqvwHgoW9EdnRQbKWV4uVnVuIa7tJ4uxYCvBRFRDnC_OITU-T91b6Nhux5ZOFe__2BSEZmXX5alezmY-q1ou8gMd7MImhF0qh57IThj9wtT7OW-t5bX8RfiobTV-xKcCmgYGArMsJLOKIeREWgi7pCofC7qI2umO5P-k432Qf5BbjEUkSVYuwtJ0EYu4GQAcCZMqMLynnoMpV7dcbFiw",
                "token_type":"bearer",
                "expires_in":300,
                "c_nonce":"-bnS9p3zQxGG5iSFqyTJUQ",
                "c_nonce_expires_in":600
            }         
            """

        every {
            applicationUtils.postRequest(any(), any(), any())
        } returns accessTokenAndNonceJson

        Mockito.doNothing().`when`(credentialRequestDataService).saveCredentialRequestData(anyString(), anyString(), anyString(), anyString())

        verifiablePresentationServiceImplTest.getCredentialIssuerMetadata(credentialOfferUriExtended, token)

        Mockito.verify(issuerDataService, times(1)).upsertIssuerData(anyString(), anyString())
        Mockito.verify(credentialRequestDataService, times(1)).saveCredentialRequestData(anyString(), anyString(), anyString(), anyString())
    }
}