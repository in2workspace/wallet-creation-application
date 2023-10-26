//package es.in2.wallet.services
//
//import es.in2.wallet.integration.orionld.service.PersonalDataSpaceService
//import es.in2.wallet.integration.orionld.service.impl.PersonalDataSpaceServiceImpl
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit.jupiter.EnabledIf
//import java.util.*
//
//@SpringBootTest
//@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'local'}", loadContext = true)
//@ContextConfiguration(classes = [PersonalDataSpaceService::class])
//class PersistenceServiceImplTest {
//
//    private lateinit var personalDataSpaceServiceImpl: PersonalDataSpaceServiceImpl
//
//    @Value("\${app.url.orion_context_broker}") private lateinit var contextBrokerEntitiesURL: String
//    private var savedVC: String = ""
//    private var credentialId: String = ""
//    private val uuid1 = UUID.randomUUID()
//    private val uuid2 = UUID.randomUUID()
//
//    @BeforeEach
//    fun setUp() {
//        // Suppose that exist a user with id test1
//        personalDataSpaceServiceImpl = PersonalDataSpaceServiceImpl(contextBrokerEntitiesURL!!)
//        // Init the url of the FIWARE Context Broker
////        personalDataSpaceServiceImpl.initUrl(contextBrokerEntitiesURL!!)
//        // Init a test VC
//        val vc1 = "eyJraWQiOiJkaWQ6a2V5OnpRM3NodGo2OTJSNmt1aTNuaDVFQ3NNakNNcVZSTjZOdXlVVjFWdnE0Qkt1eVp2U0Yje" +
//                "lEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsInR5cCI6IkpXVCIsImFsZy" +
//                "I6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpRM3NoblU5V2FZQnFRdWdxR2tqeWk3SG5DSEF3UUFrb2VVNEJyR3" +
//                "kxVGdnNFNjRXciLCJuYmYiOjE2ODQzMDYyOTUsImlzcyI6ImRpZDprZXk6elEzc2h0ajY5MlI2a3VpM25oNUVDc01" +
//                "qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsImV4cCI6MTY4Njg5ODI5NSwiaWF0IjoxNjg0MzA2Mjk1LCJ2YyI6" +
//                "eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiQ3VzdG9tZXJDcmVkZW50aWFsIl0sIkBjb250ZXh0IjpbI" +
//                "mh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sImlkIjoidXJuOnV1aWQ6ZWU2M2E2NTktNT" +
//                "BlNy00Y2E5LWEyYmYtYzA4MzZiYWVjMmM2IiwiaXNzdWVyIjoiZGlkOmtleTp6UTNzaHRqNjkyUjZrdWkzbmg1RUN" +
//                "zTWpDTXFWUk42TnV5VVYxVnZxNEJLdXladlNGIiwiaXNzdWFuY2VEYXRlIjoiMjAyMy0wNS0xN1QwNjo1MTozNVoi" +
//                "LCJpc3N1ZWQiOiIyMDIzLTA1LTE3VDA2OjUxOjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMDUtMTdUMDY6NTE6MzVaI" +
//                "iwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTA2LTE2VDA2OjUxOjM1WiIsImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOi" +
//                "JodHRwczovL2RvbWUuZXUvc2NoZW1hcy9DdXN0b21lckNyZWRlbnRpYWwvc2NoZW1hLmpzb24iLCJ0eXBlIjoiRnV" +
//                "sbEpzb25TY2hlbWFWYWxpZGF0b3IyMDIxIn0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6UTNz" +
//                "aG5VOVdhWUJxUXVncUdranlpN0huQ0hBd1FBa29lVTRCckd5MVRnZzRTY0V3IiwiZmlyc3ROYW1lIjoiVGVzdDEiL" +
//                "CJmYW1pbHlOYW1lIjoiVGVzdDEifX0sImp0aSI6InVybjp1dWlkOmVlNjNhNjU5LTUwZTctNGNhOS1hMmJmLWMwOD" +
//                "M2YmFlYzJjNiJ9.wZpf5yjW0S1SF_thtPyysCzGFDTyEmkGzI6Vmg6QtHHgO2N219See8pxtkq6JWHNe_sN7zoM6j" +
//                "3kzCtpD9tAeg"
//        personalDataSpaceServiceImpl.saveVC(uuid1, vc1)
//        savedVC = personalDataSpaceServiceImpl.getVCs(uuid1)
//        credentialId = "urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6"
//    }
//
//    @AfterEach
//    fun tearDown() {
//        // Delete the entity created in the test
//        personalDataSpaceServiceImpl.deleteVC(uuid1, credentialId)
//    }
//
//    @Test
//    fun saveVC() {
//        // Getting VC from unsaved user
//        val noneTestId = personalDataSpaceServiceImpl.getVCs(uuid2)
//        assertEquals("[]", noneTestId,
//            "The user test2 should not have any VC , because the user has not saved any VC yet")
//        //assertEquals(credentialId,"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6","The credential ID should be urn:uuid:da77f6a7-b9da-4719-a8d9-c3d1139db772")
//    }
//
//    @Test
//    fun getVCByType() {
//        // Should return the VC in JWT format
//        val vcJWT = personalDataSpaceServiceImpl.getVCByFormat(uuid1, credentialId, "vc_jwt")
//        val expectedJWT =
//            "{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_jwt\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"String\",\"value\":\"eyJraWQiOiJkaWQ6a2V5OnpRM3NodGo2OTJSNmt1aTNuaDVFQ3NNakNNcVZSTjZOdXlVVjFWdnE0Qkt1eVp2U0YjelEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpRM3NoblU5V2FZQnFRdWdxR2tqeWk3SG5DSEF3UUFrb2VVNEJyR3kxVGdnNFNjRXciLCJuYmYiOjE2ODQzMDYyOTUsImlzcyI6ImRpZDprZXk6elEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsImV4cCI6MTY4Njg5ODI5NSwiaWF0IjoxNjg0MzA2Mjk1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiQ3VzdG9tZXJDcmVkZW50aWFsIl0sIkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sImlkIjoidXJuOnV1aWQ6ZWU2M2E2NTktNTBlNy00Y2E5LWEyYmYtYzA4MzZiYWVjMmM2IiwiaXNzdWVyIjoiZGlkOmtleTp6UTNzaHRqNjkyUjZrdWkzbmg1RUNzTWpDTXFWUk42TnV5VVYxVnZxNEJLdXladlNGIiwiaXNzdWFuY2VEYXRlIjoiMjAyMy0wNS0xN1QwNjo1MTozNVoiLCJpc3N1ZWQiOiIyMDIzLTA1LTE3VDA2OjUxOjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMDUtMTdUMDY6NTE6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTA2LTE2VDA2OjUxOjM1WiIsImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RvbWUuZXUvc2NoZW1hcy9DdXN0b21lckNyZWRlbnRpYWwvc2NoZW1hLmpzb24iLCJ0eXBlIjoiRnVsbEpzb25TY2hlbWFWYWxpZGF0b3IyMDIxIn0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6UTNzaG5VOVdhWUJxUXVncUdranlpN0huQ0hBd1FBa29lVTRCckd5MVRnZzRTY0V3IiwiZmlyc3ROYW1lIjoiVGVzdDEiLCJmYW1pbHlOYW1lIjoiVGVzdDEifX0sImp0aSI6InVybjp1dWlkOmVlNjNhNjU5LTUwZTctNGNhOS1hMmJmLWMwODM2YmFlYzJjNiJ9.wZpf5yjW0S1SF_thtPyysCzGFDTyEmkGzI6Vmg6QtHHgO2N219See8pxtkq6JWHNe_sN7zoM6j3kzCtpD9tAeg\",\"metadata\":{}}}"
//        assertEquals(expectedJWT, vcJWT, "The user test1 should have one VC saved in JWT format")
//        // Should return the VC in JSON format
//        val vcJSON = personalDataSpaceServiceImpl.getVCByFormat(uuid1, credentialId, "vc_json")
//        val expectedJSON =
//            "{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_json\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"JSON\",\"value\":{\"sub\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"nbf\":1684306295,\"iss\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"exp\":1686898295,\"iat\":1684306295,\"vc\":{\"type\":[\"VerifiableCredential\",\"CustomerCredential\"],\"@context\":[\"https://www.w3.org/2018/credentials/v1\"],\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"issuer\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"issuanceDate\":\"2023-05-17T06:51:35Z\",\"issued\":\"2023-05-17T06:51:35Z\",\"validFrom\":\"2023-05-17T06:51:35Z\",\"expirationDate\":\"2023-06-16T06:51:35Z\",\"credentialSchema\":{\"id\":\"https://dome.eu/schemas/CustomerCredential/schema.json\",\"type\":\"FullJsonSchemaValidator2021\"},\"credentialSubject\":{\"id\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"firstName\":\"Test1\",\"familyName\":\"Test1\"}},\"jti\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\"},\"metadata\":{}}}"
//        assertEquals(expectedJSON, vcJSON, "The user test1 should have one VC saved in JSON format")
//    }
//
//    @Test
//    fun getVCs() {
//        // Should return the VC in both formats, and it will check if the VC have saved correctly
//        val expected =
//            "[{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_jwt\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"String\",\"value\":\"eyJraWQiOiJkaWQ6a2V5OnpRM3NodGo2OTJSNmt1aTNuaDVFQ3NNakNNcVZSTjZOdXlVVjFWdnE0Qkt1eVp2U0YjelEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpRM3NoblU5V2FZQnFRdWdxR2tqeWk3SG5DSEF3UUFrb2VVNEJyR3kxVGdnNFNjRXciLCJuYmYiOjE2ODQzMDYyOTUsImlzcyI6ImRpZDprZXk6elEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsImV4cCI6MTY4Njg5ODI5NSwiaWF0IjoxNjg0MzA2Mjk1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiQ3VzdG9tZXJDcmVkZW50aWFsIl0sIkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sImlkIjoidXJuOnV1aWQ6ZWU2M2E2NTktNTBlNy00Y2E5LWEyYmYtYzA4MzZiYWVjMmM2IiwiaXNzdWVyIjoiZGlkOmtleTp6UTNzaHRqNjkyUjZrdWkzbmg1RUNzTWpDTXFWUk42TnV5VVYxVnZxNEJLdXladlNGIiwiaXNzdWFuY2VEYXRlIjoiMjAyMy0wNS0xN1QwNjo1MTozNVoiLCJpc3N1ZWQiOiIyMDIzLTA1LTE3VDA2OjUxOjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMDUtMTdUMDY6NTE6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTA2LTE2VDA2OjUxOjM1WiIsImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RvbWUuZXUvc2NoZW1hcy9DdXN0b21lckNyZWRlbnRpYWwvc2NoZW1hLmpzb24iLCJ0eXBlIjoiRnVsbEpzb25TY2hlbWFWYWxpZGF0b3IyMDIxIn0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6UTNzaG5VOVdhWUJxUXVncUdranlpN0huQ0hBd1FBa29lVTRCckd5MVRnZzRTY0V3IiwiZmlyc3ROYW1lIjoiVGVzdDEiLCJmYW1pbHlOYW1lIjoiVGVzdDEifX0sImp0aSI6InVybjp1dWlkOmVlNjNhNjU5LTUwZTctNGNhOS1hMmJmLWMwODM2YmFlYzJjNiJ9.wZpf5yjW0S1SF_thtPyysCzGFDTyEmkGzI6Vmg6QtHHgO2N219See8pxtkq6JWHNe_sN7zoM6j3kzCtpD9tAeg\",\"metadata\":{}}},{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_json\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"JSON\",\"value\":{\"sub\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"nbf\":1684306295,\"iss\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"exp\":1686898295,\"iat\":1684306295,\"vc\":{\"type\":[\"VerifiableCredential\",\"CustomerCredential\"],\"@context\":[\"https://www.w3.org/2018/credentials/v1\"],\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"issuer\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"issuanceDate\":\"2023-05-17T06:51:35Z\",\"issued\":\"2023-05-17T06:51:35Z\",\"validFrom\":\"2023-05-17T06:51:35Z\",\"expirationDate\":\"2023-06-16T06:51:35Z\",\"credentialSchema\":{\"id\":\"https://dome.eu/schemas/CustomerCredential/schema.json\",\"type\":\"FullJsonSchemaValidator2021\"},\"credentialSubject\":{\"id\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"firstName\":\"Test1\",\"familyName\":\"Test1\"}},\"jti\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\"},\"metadata\":{}}}]"
//        assertEquals(expected, savedVC, "The user test1 should have one VC saved in two formats (JWT and JSON)")
//    }
//
//    @Test
//    fun getVCsByType() {
//        // Should return the VC in jwt format
//        val actualJWT = personalDataSpaceServiceImpl.getVCByFormat(uuid1, credentialId, "vc_jwt")
//        val expectedJWT =
//            "{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_jwt\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"String\",\"value\":\"eyJraWQiOiJkaWQ6a2V5OnpRM3NodGo2OTJSNmt1aTNuaDVFQ3NNakNNcVZSTjZOdXlVVjFWdnE0Qkt1eVp2U0YjelEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsInR5cCI6IkpXVCIsImFsZyI6IkVTMjU2SyJ9.eyJzdWIiOiJkaWQ6a2V5OnpRM3NoblU5V2FZQnFRdWdxR2tqeWk3SG5DSEF3UUFrb2VVNEJyR3kxVGdnNFNjRXciLCJuYmYiOjE2ODQzMDYyOTUsImlzcyI6ImRpZDprZXk6elEzc2h0ajY5MlI2a3VpM25oNUVDc01qQ01xVlJONk51eVVWMVZ2cTRCS3V5WnZTRiIsImV4cCI6MTY4Njg5ODI5NSwiaWF0IjoxNjg0MzA2Mjk1LCJ2YyI6eyJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiQ3VzdG9tZXJDcmVkZW50aWFsIl0sIkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIl0sImlkIjoidXJuOnV1aWQ6ZWU2M2E2NTktNTBlNy00Y2E5LWEyYmYtYzA4MzZiYWVjMmM2IiwiaXNzdWVyIjoiZGlkOmtleTp6UTNzaHRqNjkyUjZrdWkzbmg1RUNzTWpDTXFWUk42TnV5VVYxVnZxNEJLdXladlNGIiwiaXNzdWFuY2VEYXRlIjoiMjAyMy0wNS0xN1QwNjo1MTozNVoiLCJpc3N1ZWQiOiIyMDIzLTA1LTE3VDA2OjUxOjM1WiIsInZhbGlkRnJvbSI6IjIwMjMtMDUtMTdUMDY6NTE6MzVaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDIzLTA2LTE2VDA2OjUxOjM1WiIsImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RvbWUuZXUvc2NoZW1hcy9DdXN0b21lckNyZWRlbnRpYWwvc2NoZW1hLmpzb24iLCJ0eXBlIjoiRnVsbEpzb25TY2hlbWFWYWxpZGF0b3IyMDIxIn0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmtleTp6UTNzaG5VOVdhWUJxUXVncUdranlpN0huQ0hBd1FBa29lVTRCckd5MVRnZzRTY0V3IiwiZmlyc3ROYW1lIjoiVGVzdDEiLCJmYW1pbHlOYW1lIjoiVGVzdDEifX0sImp0aSI6InVybjp1dWlkOmVlNjNhNjU5LTUwZTctNGNhOS1hMmJmLWMwODM2YmFlYzJjNiJ9.wZpf5yjW0S1SF_thtPyysCzGFDTyEmkGzI6Vmg6QtHHgO2N219See8pxtkq6JWHNe_sN7zoM6j3kzCtpD9tAeg\",\"metadata\":{}}}"
//        assertEquals(expectedJWT, actualJWT, "The user test1 should have one VC saved in jwt format")
//        // Should return the VC in json format
//        val actualJSON = personalDataSpaceServiceImpl.getVCByFormat(uuid1, credentialId, "vc_json")
//        val expectedJSON =
//            "{\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"type\":\"vc_json\",\"user_ID\":{\"type\":\"String\",\"value\":\"test1\",\"metadata\":{}},\"vc\":{\"type\":\"JSON\",\"value\":{\"sub\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"nbf\":1684306295,\"iss\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"exp\":1686898295,\"iat\":1684306295,\"vc\":{\"type\":[\"VerifiableCredential\",\"CustomerCredential\"],\"@context\":[\"https://www.w3.org/2018/credentials/v1\"],\"id\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\",\"issuer\":\"did:key:zQ3shtj692R6kui3nh5ECsMjCMqVRN6NuyUV1Vvq4BKuyZvSF\",\"issuanceDate\":\"2023-05-17T06:51:35Z\",\"issued\":\"2023-05-17T06:51:35Z\",\"validFrom\":\"2023-05-17T06:51:35Z\",\"expirationDate\":\"2023-06-16T06:51:35Z\",\"credentialSchema\":{\"id\":\"https://dome.eu/schemas/CustomerCredential/schema.json\",\"type\":\"FullJsonSchemaValidator2021\"},\"credentialSubject\":{\"id\":\"did:key:zQ3shnU9WaYBqQugqGkjyi7HnCHAwQAkoeU4BrGy1Tgg4ScEw\",\"firstName\":\"Test1\",\"familyName\":\"Test1\"}},\"jti\":\"urn:uuid:ee63a659-50e7-4ca9-a2bf-c0836baec2c6\"},\"metadata\":{}}}"
//        assertEquals(expectedJSON, actualJSON, "The user test1 should have one VC saved in json format")
//    }
//
//}