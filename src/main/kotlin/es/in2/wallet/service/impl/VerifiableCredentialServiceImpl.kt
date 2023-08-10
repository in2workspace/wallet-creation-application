package es.in2.wallet.service.impl

import com.nimbusds.jose.JWSHeader
import com.nimbusds.jwt.JWTClaimsSet
import VcTemplateDeserializer
import java.time.Instant
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.databind.module.SimpleModule
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.exception.CredentialRequestDataNotFoundException
import es.in2.wallet.model.W3CContextDeserializer
import es.in2.wallet.model.W3CCredentialSchemaDeserializer
import es.in2.wallet.model.W3CIssuerDeserializer
import es.in2.wallet.service.*
import java.util.*
import es.in2.wallet.util.*
import es.in2.wallet.util.ApplicationUtils.buildUrlEncodedFormDataRequestBody
import es.in2.wallet.util.ApplicationUtils.getRequest
import es.in2.wallet.util.ApplicationUtils.postRequest
import id.walt.credentials.w3c.W3CContext
import id.walt.credentials.w3c.W3CCredentialSchema
import id.walt.credentials.w3c.W3CIssuer
import id.walt.credentials.w3c.templates.VcTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.nimbusds.jose.jwk.ECKey
import es.in2.wallet.exception.IssuerDataNotFoundException
import es.in2.wallet.model.dto.*
import org.springframework.stereotype.Service

@Service
class VerifiableCredentialServiceImpl(
    private val personalDataSpaceService: PersonalDataSpaceService,
    private val issuerDataService: AppIssuerDataService,
    private val credentialRequestDataService: AppCredentialRequestDataService,
    private val walletKeyService: WalletKeyService

) : VerifiableCredentialService {

    private val log: Logger = LogManager.getLogger(VerifiableCredentialServiceImpl::class.java)

    override fun getCredentialIssuerMetadata(credentialOfferUriExtended: String) {
        val credentialOfferUri = getCredentialOfferUri(credentialOfferUriExtended)
        val credentialOffer = getCredentialOffer(credentialOfferUri)
        val credentialIssuerMetadataUri = getCredentialIssuerMetadataUri(credentialOffer)
        try {
            val credentialIssuerMetadataObject = getCredentialIssuerMetadataObject(credentialIssuerMetadataUri)
            issuerDataService.saveIssuerData(credentialOffer.credentialIssuer,credentialIssuerMetadataObject.toString())
            val accessToken = getAccessTokenAndNonce(credentialOffer, credentialIssuerMetadataObject)
            credentialRequestDataService.saveCredentialRequestData(credentialOffer.credentialIssuer,accessToken[0],accessToken[1])
        }catch (e: UnrecognizedPropertyException){
            log.error(e)
            val credentialIssuerMetadataObject = getCredentialIssuerMetadataObject1(credentialIssuerMetadataUri)
            issuerDataService.saveIssuerData(credentialOffer.credentialIssuer,credentialIssuerMetadataObject.toString())
            val accessToken = getAccessTokenAndNonce1(credentialOffer, credentialIssuerMetadataObject)
            credentialRequestDataService.saveCredentialRequestData(credentialOffer.credentialIssuer,accessToken[0],accessToken[1])
        }
    }

    override fun getVerifiableCredential(credentialRequestDTO: CredentialRequestDTO) {
        val jwt = createJwt(credentialRequestDTO)
        log.debug("jwt object: $jwt")
        credentialRequestDataService.clearIssuerNonceByIssuerName(credentialRequestDTO.issuerName)
        val credentialRequestBody = createCredentialRequestBody(credentialRequestDTO.proofType,jwt)
        val accessToken = getExistentAccessToken(credentialRequestDTO.issuerName)
        val issuerMetadata = ObjectMapper().readTree(getExistentMetadata(credentialRequestDTO.issuerName))
        val verifiableCredential = getVerifiableCredential1(accessToken,issuerMetadata,credentialRequestBody)
        log.debug("verifiable credential: $verifiableCredential")
        personalDataSpaceService.saveVC(verifiableCredential)

    }


    /**
     * @param credentialOfferUriExtended:
     *  Example of Credential Offer URI for Pre-Authorized Code Flow using DOME standard:
     *  https://www.goodair.com/credential-offer?credential_offer_uri=
     *  https://www.goodair.com/credential-offer/5j349k3e3n23j
     */
    private fun getCredentialOfferUri(credentialOfferUriExtended: String): String {
        val splitCredentialOfferUri = credentialOfferUriExtended.split("=")
        val credentialOfferUriValue = splitCredentialOfferUri[1]
        log.debug("Credential offer URI: {}", credentialOfferUriValue)
        return credentialOfferUriValue
    }

    private fun getCredentialOffer(credentialOfferUri: String): CredentialOfferForPreAuthorizedCodeFlow {
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val response = getRequest(url=credentialOfferUri, headers=headers)
        val valueTypeRef = ObjectMapper().typeFactory.constructType(CredentialOfferForPreAuthorizedCodeFlow::class.java)
        val credentialOffer: CredentialOfferForPreAuthorizedCodeFlow = ObjectMapper().readValue(response, valueTypeRef)
        log.debug("Credential offer: {}", credentialOffer)
        return credentialOffer
    }

    /**
     * Generate dynamic URL to get the credential_issuer_metadata
     */
    private fun getCredentialIssuerMetadataUri(credentialOffer: CredentialOfferForPreAuthorizedCodeFlow): String {
        return credentialOffer.credentialIssuer + "/.well-known/openid-credential-issuer"
    }

    /*
        TODO: Deserialization is encountering error in VerifiableCredential
     */
    private fun getCredentialIssuerMetadataObject(credentialIssuerMetadataUri: String): CredentialIssuerMetadata {
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val response = getRequest(url=credentialIssuerMetadataUri, headers=headers)
        val objectMapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(VcTemplate::class.java, VcTemplateDeserializer())
        module.addDeserializer(W3CContext::class.java, W3CContextDeserializer())
        module.addDeserializer(W3CIssuer::class.java, W3CIssuerDeserializer())
        module.addDeserializer(W3CCredentialSchema::class.java, W3CCredentialSchemaDeserializer())
        objectMapper.registerModule(module)
        val valueTypeRef = objectMapper.typeFactory.constructType(CredentialIssuerMetadata::class.java)
        val credentialIssuerMetadata: CredentialIssuerMetadata = objectMapper.readValue(response, valueTypeRef)
        log.debug("Credential Issuer Metadata: {}", credentialIssuerMetadata)
        return credentialIssuerMetadata
    }

    private fun getCredentialIssuerMetadataObject1(credentialIssuerMetadataUri: String): JsonNode {
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val response = getRequest(url=credentialIssuerMetadataUri, headers=headers)
        val credentialIssuerMetadata = ObjectMapper().readTree(response)
        log.debug("Credential Issuer Metadata: {}", credentialIssuerMetadata)
        return credentialIssuerMetadata
    }

    private fun getAccessTokenAndNonce(credentialOffer: CredentialOfferForPreAuthorizedCodeFlow,
                               credentialIssuerMetadata: CredentialIssuerMetadata): List<String>{
        val tokenEndpoint = credentialIssuerMetadata.credentialToken
        val preAuthorizedCodeObject = credentialOffer.grants[PRE_AUTH_CODE_GRANT_TYPE]
        val preAuthorizedCode = preAuthorizedCodeObject?.preAuthorizedCode
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val formData = mapOf("grant_type" to PRE_AUTH_CODE_GRANT_TYPE, "pre-authorized_code" to preAuthorizedCode)
        val body = buildUrlEncodedFormDataRequestBody(formDataMap=formData)
        val response = postRequest(url=tokenEndpoint, headers=headers, body=body)
        val accessTokenAndNonceJson: JsonNode = ObjectMapper().readTree(response)
        log.debug("Access token and nonce value: $accessTokenAndNonceJson")
        val accessToken = accessTokenAndNonceJson["access_token"].asText()
        val cNonce = accessTokenAndNonceJson["c_nonce"].asText()
        return listOf(cNonce,accessToken)
    }

    private fun getAccessTokenAndNonce1(credentialOffer: CredentialOfferForPreAuthorizedCodeFlow,
                               credentialIssuerMetadata: JsonNode): List<String>{
        val tokenEndpoint = credentialIssuerMetadata["credential_token"].asText()
        val preAuthorizedCodeObject = credentialOffer.grants[PRE_AUTH_CODE_GRANT_TYPE]
        val preAuthorizedCode = preAuthorizedCodeObject?.preAuthorizedCode
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val formData = mapOf("grant_type" to PRE_AUTH_CODE_GRANT_TYPE, "pre-authorized_code" to preAuthorizedCode)
        val body = buildUrlEncodedFormDataRequestBody(formDataMap=formData)
        val response = postRequest(url=tokenEndpoint, headers=headers, body=body)
        val accessTokenAndNonceJson: JsonNode = ObjectMapper().readTree(response)
        log.debug("Access token and nonce value: $accessTokenAndNonceJson")
        val accessToken = accessTokenAndNonceJson["access_token"].asText()
        val cNonce = accessTokenAndNonceJson["c_nonce"].asText()
        return listOf(cNonce,accessToken)
    }

    private fun getVerifiableCredential(accessToken: String, credentialOffer: CredentialOfferForPreAuthorizedCodeFlow,
                                        credentialIssuerMetadata: CredentialIssuerMetadata): String {
        val credentialType = credentialOffer.credentials[0]
        val credentialEndpoint = credentialIssuerMetadata.credentialEndpoint + credentialType
        val headers = listOf(
            CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM,
            HEADER_AUTHORIZATION to "Bearer $accessToken")
        val verifiableCredential = postRequest(url=credentialEndpoint, headers=headers, body="")
        log.debug("Verifiable credential: {}", verifiableCredential)
        return verifiableCredential
    }

    private fun getVerifiableCredential1(accessToken: String, credentialIssuerMetadata: JsonNode,credentialRequestBodyDTO: CredentialRequestBodyDTO): String {
        val credentialEndpoint = credentialIssuerMetadata["credential_endpoint"].asText()
        val headers = listOf(
            CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON,
            HEADER_AUTHORIZATION to "Bearer $accessToken")
        val objectMapper = ObjectMapper()
        val requestBodyJson = objectMapper.writeValueAsString(credentialRequestBodyDTO)
        val body = requestBodyJson.toString()
        val verifiableCredential = postRequest(url=credentialEndpoint, headers=headers, body=body)
        log.debug("Verifiable credential: {}", verifiableCredential)
        return verifiableCredential
    }

    private fun createJwt(credentialRequestDTO: CredentialRequestDTO): String{
        val ecJWK : ECKey = walletKeyService.getECKeyFromKid(credentialRequestDTO.did)
        val signer: JWSSigner = ECDSASigner(ecJWK)
        val header = createJwtHeader(credentialRequestDTO.did)
        val payload = createJwtPayload(credentialRequestDTO.issuerName)
        val signedJWT = SignedJWT(header, payload)
        signedJWT.sign(signer)
        log.debug("JWT signed successfully")
        return signedJWT.serialize()
    }
    private fun createJwtHeader(kid: String): JWSHeader {
        return JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType("openid4vci-proof+jwt"))
                .keyID(kid)
                .build()
    }

    private fun createJwtPayload(issuerName: String): JWTClaimsSet {
        val instant = Instant.now()
        val requestData = credentialRequestDataService.getCredentialRequestDataByIssuerName(issuerName)
        val nonce = requestData.map { it.issuerNonce }
                .orElseThrow { CredentialRequestDataNotFoundException("Nonce not found for $issuerName") }
        return JWTClaimsSet.Builder()
                .audience(issuerName)
                .issueTime(Date.from(instant))
                .claim("nonce", nonce)
                .build()
    }

    private fun createCredentialRequestBody(proofType: String, jwt: String): CredentialRequestBodyDTO{
        val proof = ProofDTO(proofType,jwt)
        return CredentialRequestBodyDTO("jwt_vc_json",proof)
    }

    private fun getExistentAccessToken(issuerName: String): String {
        val requestData = credentialRequestDataService.getCredentialRequestDataByIssuerName(issuerName)
        return requestData.map { it.issuerAccessToken }
                .orElseThrow { CredentialRequestDataNotFoundException("Access token not found for $issuerName") }
    }

    private fun getExistentMetadata(issuerName: String): String {
        val requestData = issuerDataService.getIssuerDataByIssuerName(issuerName)
        return requestData.map { it.metadata }
                .orElseThrow { IssuerDataNotFoundException("Issuer metadata not found for $issuerName") }
    }
}
