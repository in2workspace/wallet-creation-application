package es.in2.wallet.wca.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.JWSObject
import es.in2.wallet.api.util.*
import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.TokenVerificationService
import id.walt.credentials.w3c.VerifiablePresentation
import id.walt.model.dif.DescriptorMapping
import id.walt.model.dif.PresentationSubmission
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Service

@Service
class SiopServiceImpl(
    private val tokenVerificationService: TokenVerificationService,
    private val orionService: OrionService,
    private val applicationUtils: ApplicationUtils
) : SiopService {

    private val log: Logger = LogManager.getLogger(SiopServiceImpl::class.java)

    /**
     * This method executes the siop_authentication_request_uri to get a SIOP Authentication Request
     * as a JWT in JWS format.
     */
    override fun getSiopAuthenticationRequest(siopAuthenticationRequestUri: String): VcSelectorRequestDTO {
        log.info("SiopServiceImpl.getSiopAuthenticationRequest()")

        // get SIOP Authentication Request as JWT in JWS form by executing the received URI
        val jwtSiopAuthRequest = getSiopAuthenticationRequestInJwsFormat(siopAuthenticationRequestUri)

        // verify the received response
        tokenVerificationService.verifySiopAuthRequestAsJwsFormat(jwtSiopAuthRequest)

        // Extract "auth_request" of the JWT which contains the SIOP Authentication Request
        val siopAuthenticationRequest = getAuthRequestClaim(jwtSiopAuthRequest)

        return processSiopAuthenticationRequest(siopAuthenticationRequest)
    }

    private fun getSiopAuthenticationRequestInJwsFormat(siopAuthenticationRequestUri: String): String {
        return applicationUtils.getRequest(url=siopAuthenticationRequestUri, headers=listOf())
    }

    /**
     * This method processes the received SIOP Authentication Request. This SIOP Authentication Request
     * can be received as a result of a previous method - getSiopAuthenticationRequestInJwsFormat() - or
     * as a result of the identification of QR content.
     */
    override fun processSiopAuthenticationRequest(siopAuthenticationRequest: String): VcSelectorRequestDTO {

        val parsedSiopAuthenticationRequest = ApplicationUtils.parseOpenIdConfig(siopAuthenticationRequest)

        // Extract the scope claim of the SIOP Authentication Request
        val scopeList = extractScopeClaimOfTheSiopAuthRequest(siopAuthenticationRequest)

        // Find if User has a Verifiable Credential that matches with all the scopes requested
        val selectableVcList = orionService.getSelectableVCsByVcTypeList(scopeList)

        // Populate the response to the Wallet Front-End adding the SIOP Authentication Request and a List of the
        // Verifiable Credential IDs that match with the requested scope
        return VcSelectorRequestDTO(
            redirectUri = parsedSiopAuthenticationRequest.redirectUri,
            state = parsedSiopAuthenticationRequest.state,
            selectableVcList = selectableVcList
        )
    }

    override fun sendAuthenticationResponse(vcSelectorResponseDTO: VcSelectorResponseDTO, vp: String): String {
        log.info("SiopServiceImpl.sendAuthenticationResponse()")
        val descriptorMap = generateDescriptorMap(vp)
        log.info("Generating Presentation Submission")
        val presentationSubmission = PresentationSubmission(
            descriptor_map = listOf(descriptorMap),
            definition_id = "CustomerPresentationDefinition",
            id = "CustomerPresentationSubmission"
        )
        val presentationSubmissionString = parserPresentationSubmissionToString(presentationSubmission)
        // Parse de String SIOP Authentication Response to a readable JSON Object
        val formData = "state=${vcSelectorResponseDTO.state}" +
                       "&vp_token=$vp" +
                       "&presentation_submission=$presentationSubmissionString"

        log.info("RedirectUri: "+vcSelectorResponseDTO.redirectUri)
        log.info("FormData: $formData")
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_URL_ENCODED_FORM)
        val response = ApplicationUtils.postRequest(url=vcSelectorResponseDTO.redirectUri,
            headers=headers, body=formData)
        log.info("response body = {}", response)
        // access_token returned
        return response
    }

    /**
     * This method parser the presentation submission map to a String
     * @param presentationSubmission
     * @return String
     */
    private fun parserPresentationSubmissionToString(presentationSubmission: PresentationSubmission): String {
        return ObjectMapper().writeValueAsString(presentationSubmission)
    }

    /**
     * This method generate a Descriptor Mapping from a Verifiable Presentation
     * @param vp
     * @return DescriptorMapping
     * This function only works with JWT_VP and JWT_VC formats, because the path assignation is different with other formats
     * In the verifier component, can read this format and extract the information of the Verifiable Presentation
     */
    private fun generateDescriptorMap(vp: String): DescriptorMapping {

        val verifiablePresentation = VerifiablePresentation.fromString(vp)
        val verifiableCredential = verifiablePresentation.verifiableCredential!!
        var credentialDescriptorMap: DescriptorMapping? = null
        verifiableCredential.forEachIndexed() { index, it ->
            val tmpCredentialDescriptorMap = DescriptorMapping(
                format = JWT_VC,
                id = it.id,
                path = "$.verifiableCredential[$index]"
            )
            credentialDescriptorMap = if (credentialDescriptorMap == null) {
                tmpCredentialDescriptorMap
            } else {
                addCredentialDescriptorMap(credentialDescriptorMap, tmpCredentialDescriptorMap)
            }

        }

        return DescriptorMapping(
            format = JWT_VP,
            id = verifiablePresentation.id,
            path = "$",
            path_nested = credentialDescriptorMap
        )
    }
    /**
     * This an auxiliary method to add a Descriptor Mapping to a Descriptor Mapping in the first available path_nested
     */

    private fun addCredentialDescriptorMap(
        credentialDescriptorMap: DescriptorMapping?,
        tmpCredentialDescriptorMap: DescriptorMapping
    ): DescriptorMapping {
        return if (credentialDescriptorMap!!.path_nested == null) {
            credentialDescriptorMap.copy(path_nested = tmpCredentialDescriptorMap)
        } else {
            addCredentialDescriptorMap(credentialDescriptorMap.path_nested, tmpCredentialDescriptorMap)
        }
    }


    //TODO: we are using payload.toJSONObject in many places, we should parse it only once
    private fun getAuthRequestClaim(jwtSiopAuthRequest: String): String {
        val jwsObject = JWSObject.parse(jwtSiopAuthRequest)
        return jwsObject.payload.toJSONObject()["auth_request"].toString()
    }

    private fun extractScopeClaimOfTheSiopAuthRequest(siopAuthenticationRequest: String): List<String> {
        val scopeRegex = Regex("scope=\\[([^]]+)]")
        val scope = scopeRegex.find(siopAuthenticationRequest)
        // Check if scope is null, if it is null use the default, if not, use the scope list attached
        return if (scope != null) {
            scope.groupValues[1].split(",")
        } else {
            listOf("VerifiableId")
        }
    }

}