package es.in2.wallet.wca.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.api.security.exception.InvalidTokenException
import es.in2.wallet.wca.exception.JwtInvalidFormatException
import es.in2.wallet.wca.service.TokenVerificationService
import es.in2.wallet.api.util.ISSUER_SUB
import es.in2.wallet.api.util.ISSUER_TOKEN_PROPERTY_NAME
import id.walt.services.did.DidService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service
import java.security.interfaces.ECPublicKey
import java.text.ParseException

@Service
class TokenVerificationServiceImpl : TokenVerificationService {

    private val log: Logger = LogManager.getLogger(TokenVerificationServiceImpl::class.java)

    override fun verifySiopAuthRequestAsJwsFormat(requestToken: String) {
        log.info("RequestTokenVerificationServiceImpl - verifyRequestToken()")
        val signedJWTResponse = parseRequestTokenToSignedJwt(requestToken)
        // resolveDID() implements a new business logic that, if Universal Resolver is down,
        // we resolve the DID value using the SSI Kit. It only works with did:key methods
        val didDocument = resolveDID(signedJWTResponse.payload)
        val ecPublicKey = generateEcPublicKeyFromDidDocument(didDocument)
        val verifier = verifySignedJwtWithPublicEcKey(ecPublicKey)
        checkJWSVerifierResponse(signedJWTResponse, verifier)
    }

    private fun parseRequestTokenToSignedJwt(requestToken: String): SignedJWT {
        log.info("RequestTokenVerificationServiceImpl - parseRequestTokenToSignedJwt()")
        try {
            return SignedJWT.parse(requestToken)
        } catch (e: ParseException) {
            throw JwtInvalidFormatException("The 'request_token' has an invalid format")
        }
    }

    private fun resolveDID(payload: Payload): JsonNode {
        log.info("RequestTokenVerificationServiceImpl - resolveDID()")

        val jsonPayload: MutableMap<String, Any> = payload.toJSONObject()
        val issuerDID: String = jsonPayload[ISSUER_TOKEN_PROPERTY_NAME].toString()
        val sub: String = jsonPayload[ISSUER_SUB].toString()
        val clientId = getClientId(jsonPayload)
        log.info("clientId: $clientId")
        if (clientId != issuerDID || issuerDID != sub){
            throw Exception("iss and sub MUST be the DID of the RP and must correspond to the client_id parameter in the Authorization Request")
        }
        log.info("issuer_did = $issuerDID")

        log.info("Resolving DID using SSI Kit")
        val didDocument = DidService.resolve(issuerDID)
        log.debug("didDocument = ${didDocument.encodePretty()}")
        return ObjectMapper().readTree(didDocument.encodePretty())

        /*
            Until we can use our Universal Resolver we do not need to use this code:
                try {
                    log.info("Resolving DID using Universal Resolver")
                    val url = "$UNIVERSAL_RESOLVER_URL/$issuerDID"
                    val request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .headers("Content-Type", "application/json")
                        .GET()
                        .build()
                    val response = HttpClient.newBuilder()
                        .build()
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    if (response.get().statusCode() != 200) {
                        log.error("Request cannot be completed. HttpStatus response ${response.get().statusCode()}")
                        throw Exception("Request cannot be completed. HttpStatus response ${response.get().statusCode()}")
                    }
                    val result = response.get().body()
                    if (result.isNotBlank()) {
                        log.debug("didDocument = $result")
                        return ObjectMapper().readTree(result)["didDocument"]
                    } else {
                        log.error("The DID $issuerDID is not in the Trusted Participant List")
                        throw DidVerificationException("The DID $issuerDID is not in the Trusted Participant List")
                    }
                } catch (e: Exception) {
                    log.info("Resolving DID using SSI Kit")
                    val didDocument = DidService.resolve(issuerDID)
                    log.debug("didDocument = ${didDocument.encodePretty()}")
                    return ObjectMapper().readTree(didDocument.encodePretty())
                }
        */

    }

    private fun getClientId(siopAuthenticationRequest: MutableMap<String, Any>): String? {
        val authRequest:String = siopAuthenticationRequest["auth_request"].toString()
        val scopeRegex = Regex("client_id=([^&]+)")
        return scopeRegex.find(authRequest)?.groupValues?.get(1)
    }

    private fun generateEcPublicKeyFromDidDocument(didDocument: JsonNode): ECPublicKey {
        log.info("RequestTokenVerificationServiceImpl - generateEcPublicKeyFromDidDocument()")
        val verificationMethod = didDocument["verificationMethod"]
        val verificationMethodIndex0 = verificationMethod[0]
        val publicKeyJwk = verificationMethodIndex0["publicKeyJwk"].toString()
        return ECKey.parse(publicKeyJwk).toECPublicKey()
    }

    private fun verifySignedJwtWithPublicEcKey(ecPublicKey: ECPublicKey): JWSVerifier {
        log.info("RequestTokenVerificationServiceImpl - verifySignedJwtWithPublicEcKey()")
        try {
            return ECDSAVerifier(ecPublicKey)
        } catch (e: JOSEException) {
            throw InvalidTokenException("We have not been able to verify the 'request_token'")
        }
    }

    private fun checkJWSVerifierResponse(signedJWTResponse: SignedJWT, verifier: JWSVerifier) {
        log.info("RequestTokenVerificationServiceImpl - checkJWSVerifierResponse()")
        if (!signedJWTResponse.verify(verifier)) {
            throw JwtInvalidFormatException("The 'request_token' is not valid")
        }
    }

}