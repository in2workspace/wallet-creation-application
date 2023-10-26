package es.in2.wallet.integration.orion.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.SignedJWT
import es.in2.wallet.api.exception.NoSuchVerifiableCredentialException
import es.in2.wallet.api.service.AppUserService
import es.in2.wallet.api.util.*
import es.in2.wallet.api.util.ApplicationUtils.postRequest
import es.in2.wallet.integration.orion.model.DidEntity
import es.in2.wallet.integration.orion.model.OrionAttribute
import es.in2.wallet.integration.orion.model.VerifiableCredentialEntity
import es.in2.wallet.integration.orion.service.OrionService
import es.in2.wallet.wca.exception.DIDNotFoundException
import es.in2.wallet.wca.exception.InvalidDidFormatException
import es.in2.wallet.wca.model.dto.DidResponseDTO
import es.in2.wallet.wca.model.dto.VcBasicDataDTO
import es.in2.wallet.wca.model.entity.DidMethods
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URLEncoder
import java.util.*

@Service
class OrionServiceImpl(
    private val appUserService: AppUserService,
    private val applicationUtils: ApplicationUtils,
    @Value("\${app.url.orion_context_broker}") private val contextBrokerEntitiesURL: String
) : OrionService {

    private val log: Logger = LoggerFactory.getLogger(OrionServiceImpl::class.java)

    override fun saveVC(vcJwt: String) {
        val userId = getUserIdFromContextAuthentication()
        val vcJson = extractVcJsonFromVcJwt(vcJwt)
        val vcId = extractVerifiableCredentialIdFromVcJson(vcJson)
        val userIdAttribute = OrionAttribute(type = STRING_FORMAT, value = userId)
        val vcJwtAttribute = OrionAttribute(type = STRING_FORMAT, value = vcJwt)
        val vcJsonAttribute = OrionAttribute(type = JSON_FORMAT, value = vcJson)
        // Log the start of the saveVC function
        log.info("Saving Verifiable Credential for user: $userId")
        // Store the VC as VC_JWT in Context Broker
        storeVcInContextBroker(
            VerifiableCredentialEntity(id = vcId, type = VC_JWT, userId = userIdAttribute, vcData = vcJwtAttribute)
        )
        // Store the VC as VC_JSON in Context Broker
        storeVcInContextBroker(
            VerifiableCredentialEntity(id = vcId, type = VC_JSON, userId = userIdAttribute, vcData = vcJsonAttribute)
        )
        // Log the successful completion of the saveVC function
        log.info("Verifiable Credential saved successfully for user: $userId")
    }

    private fun getUserIdFromContextAuthentication(): String {
        // Retrieve the user session using context authentication
        val userSession = appUserService.getUserWithContextAuthentication()
        // Extract and return the user ID as a string
        val userId = userSession.id!!.toString()
        // Log the user ID extraction
        log.debug("User ID extracted from context authentication: {}", userId)
        return userId
    }

    private fun extractVcJsonFromVcJwt(vcJwt: String): JsonNode {
        // Parse the vc_jwt into a readable JWT object
        val parsedVcJwt = SignedJWT.parse(vcJwt)
        // Get the payload from vc_jwt
        val jsonObject = ObjectMapper().readTree(parsedVcJwt.payload.toString())
        // Get the 'vc' claim of the payload
        val vcJson = jsonObject["vc"]
        // Log the VC JSON extraction
        log.debug("Verifiable Credential JSON extracted from VC JWT: {}", vcJson)
        return vcJson
    }

    private fun extractVerifiableCredentialIdFromVcJson(vcJson: JsonNode): String {
        // Get the ID of the Verifiable Credential and verify it is not null or empty
        val vcId = vcJson["id"].asText()
        checkIfCredentialIdIsNull(vcId)
        // Log the Verifiable Credential ID extraction
        log.debug("Verifiable Credential ID extracted: {}", vcId)
        return vcId
    }

    private fun checkIfCredentialIdIsNull(vcId: String) {
        if (vcId.isBlank()) {
            log.error("Verifiable Credential does not contain an ID")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Verifiable Credential does not contain an ID")
        }
    }

    private fun storeVcInContextBroker(contextBrokerEntity: VerifiableCredentialEntity) {
        val url = contextBrokerEntitiesURL
        val requestBody = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(contextBrokerEntity)
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON)
        postRequest(url = url, headers = headers, body = requestBody)
        log.info("Verifiable Credential stored in Context Broker")
    }

    override fun getUserVCsInJson(): MutableList<VcBasicDataDTO> {
        val result: MutableList<VcBasicDataDTO> = mutableListOf()
        val contextBrokerVcList = getVerifiableCredentialsByUserIdAndFormat(VC_JSON)
        contextBrokerVcList.forEach {
            val vcDataValue = it.vcData.value as LinkedHashMap<*, *>
            val jsonNode = ObjectMapper().convertValue(vcDataValue, JsonNode::class.java)
            val vcTypeList = getVcTypeListFromVcJson(jsonNode)
            result.add(
                VcBasicDataDTO(
                    id = it.id,
                    vcType = vcTypeList,
                    credentialSubject = jsonNode["credentialSubject"]
                )
            )
        }
        return result
    }

    override fun getSelectableVCsByVcTypeList(vcTypeList: List<String>): List<VcBasicDataDTO> {

        val result = mutableListOf<VcBasicDataDTO>()
        val vcListInJsonByUser = getVerifiableCredentialsByUserIdAndFormat(VC_JSON)

        vcListInJsonByUser.forEach {
            // Parse the VC stored into a JsonNode object
            val vcDataValue = it.vcData.value as LinkedHashMap<*, *>
            val jsonNode = ObjectMapper().convertValue(vcDataValue, JsonNode::class.java)

            // Create a list of the VC IDs
            val vcDataTypeList = getVcTypeListFromVcJson(jsonNode)
            // If vc_types matches with vc_types requested, build VcBasicDataDTO and store it in a List
            if (vcDataTypeList.containsAll(vcTypeList)) {
                result.add(
                    VcBasicDataDTO(
                        id = jsonNode["id"].asText(),
                        vcType = vcDataTypeList,
                        credentialSubject = jsonNode["credentialSubject"]
                    )
                )
            }
        }

        checkIfResultIsEmpty(result)

        return result
    }

    private fun checkIfResultIsEmpty(result: MutableList<VcBasicDataDTO>) {
        if (result.isEmpty()) {
            throw NoSuchVerifiableCredentialException("There is no Verifiable Credential stored in Context Broker")
        }
    }

    override fun deleteVerifiableCredential(id: String) {
        applicationUtils.deleteRequest(url = "$contextBrokerEntitiesURL/$id?type=$VC_JWT", headers = listOf())
        applicationUtils.deleteRequest(url = "$contextBrokerEntitiesURL/$id?type=$VC_JSON", headers = listOf())
    }

    fun getVerifiableCredentialsByUserIdAndFormat(format: String): MutableList<VerifiableCredentialEntity> {
        val userUUID = getUserIdFromContextAuthentication()
        val response = applicationUtils.getRequest(
            url = "$contextBrokerEntitiesURL?type=$format&q=userId==$userUUID",
            headers = listOf()
        )
        return parseResponseBodyIntoContextBrokerVcMutableList(response)
    }

    private fun parseResponseBodyIntoContextBrokerVcMutableList(response: String): MutableList<VerifiableCredentialEntity> {
        val result: MutableList<VerifiableCredentialEntity> = mutableListOf()
        JSONArray(response).forEach {
            val contextBrokerVcEntityDTO =
                ObjectMapper().readValue(it.toString(), VerifiableCredentialEntity::class.java)
            result.add(contextBrokerVcEntityDTO)
        }
        return result
    }

    private fun getVcTypeListFromVcJson(jsonNode: JsonNode): MutableList<String> {
        val result = mutableListOf<String>()
        jsonNode["type"].forEach { result.add(it.asText()) }
        return result
    }

    override fun getVerifiableCredentialByIdAndFormat(id: String, format: String): String {
        // Get user session
        val userUUID = getUserIdFromContextAuthentication()
        val response = applicationUtils.getRequest(
            url = "$contextBrokerEntitiesURL/$id?type=$format&q=userId==$userUUID",
            headers = listOf()
        )
        val objectMapper = ObjectMapper()
        return if (format == VC_JWT) {
            objectMapper.readValue(response, VerifiableCredentialEntity::class.java).vcData.value.toString()
        } else {
            objectMapper.writeValueAsString(
                objectMapper.readValue(
                    response,
                    VerifiableCredentialEntity::class.java
                ).vcData.value
            )
        }
    }

    override fun deleteVCs() {
        val userUUID = getUserIdFromContextAuthentication()
        val typePattern = URLEncoder.encode("^vc", "UTF-8")
        val response = applicationUtils.getRequest(
            url = "$contextBrokerEntitiesURL?typePattern=$typePattern&q=userId==$userUUID",
            headers = listOf()
        )
        val vcs = parseResponseBodyIntoContextBrokerVcMutableList(response)
        val vcIdList = getDistinctIds(vcs)
        // get all VCs from user
        vcIdList.forEach {
            // delete VCs by Id
            deleteVerifiableCredential(it)
        }
    }

    @Throws(InvalidDidFormatException::class)
    override fun saveDid(did: String, didMethod: DidMethods) {
        val userId = getUserIdFromContextAuthentication()
        val userIdAttribute = OrionAttribute(type = STRING_FORMAT, value = userId)
        // Log the start of saveDID func
        log.debug("Saving the new DID $did for user: $userId")

        if (didMethod == DidMethods.DID_ELSI && !did.startsWith("did:elsi:")) {

            throw InvalidDidFormatException("DID does not match the pattern")

        }

        storeDIDInContextBroker(
            DidEntity(id = did, type = didMethod.stringValue, userId = userIdAttribute)
        )

        log.info("DID Stored Successfully")
    }


    private fun storeDIDInContextBroker(didEntity: DidEntity) {
        val url = contextBrokerEntitiesURL
        val requestBody = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(didEntity)
        val headers = listOf(CONTENT_TYPE to CONTENT_TYPE_APPLICATION_JSON)
        applicationUtils.postRequest(url = url, body = requestBody, headers = headers)
        log.info("DID Stored in Context Broker")
    }

    override fun getDidsByUserId(): MutableList<DidResponseDTO> {
        val userUUID = getUserIdFromContextAuthentication()
        // We need to encode ^ character to avoid interpretation issues in the HTTP request
        val typePattern = URLEncoder.encode("^did", "UTF-8")
        // get all DIDs from user
        val response = applicationUtils.getRequest(
            url = "$contextBrokerEntitiesURL/?typePattern=$typePattern&q=userId==$userUUID", headers = listOf()
        )
        return parseResponseBodyIntoContextBrokerDidMutableList(response)

    }

    private fun parseResponseBodyIntoContextBrokerDidMutableList(response: String): MutableList<DidResponseDTO> {
        val result: MutableList<DidResponseDTO> = mutableListOf()
        JSONArray(response).forEach {
            val jsonObject = JSONObject(it.toString())
            val did = jsonObject.getString("id")
            val didResponseDTO = DidResponseDTO(did)
            result.add(didResponseDTO)
        }
        return result
    }

    override fun deleteSelectedDid(didResponseDTO: DidResponseDTO) {
        val userUUID = getUserIdFromContextAuthentication()
        didExists(didResponseDTO, userUUID)
        applicationUtils.deleteRequest(
            url = "$contextBrokerEntitiesURL/${didResponseDTO.did}?userId.value=$userUUID",
            headers = listOf()
        )
    }

    private fun didExists(didResponseDTO: DidResponseDTO, userUUID: String): Boolean {
        try {
            val response = applicationUtils.getRequest(
                url = "$contextBrokerEntitiesURL/${didResponseDTO.did}?userId.value=$userUUID",
                headers = listOf()
            )
            return response.isNotEmpty()

        } catch (e: NoSuchElementException) {
            throw DIDNotFoundException("DID not found: ${didResponseDTO.did}")
        }
    }


    fun getDistinctIds(vcs: MutableList<VerifiableCredentialEntity>): List<String> {
        return vcs.map { it.id }.distinct()
    }

}