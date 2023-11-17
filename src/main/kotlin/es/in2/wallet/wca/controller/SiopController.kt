package es.in2.wallet.wca.controller

import es.in2.wallet.wca.exception.NoAuthorizationFoundException
import es.in2.wallet.wca.model.dto.QrContentDTO
import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.service.SiopService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/siop")
class SiopController (
        private val siopService: SiopService
){
    private val log: Logger = LogManager.getLogger(SiopController::class.java)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Siop Authentication Request",
        description = "Get Siop Authentication Request"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Authentication data retrieved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getSiopAuthenticationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String, @RequestBody qrContentDTO: QrContentDTO): VcSelectorRequestDTO {
        if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer ")) {
            val errorMessage = "No Bearer token found in Authorization header"
            throw NoAuthorizationFoundException(errorMessage)
        }
        val token = authorizationHeader.substring(7)
        log.info("siopService.getSiopAuthenticationRequest()")
        return siopService.getSiopAuthenticationRequest(qrContentDTO.content, token)
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Siop Authentication Request",
        description = "Process Siop Authentication Request"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Authentication data retrieved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun processSiopAuthenticationRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String, @RequestBody qrContentDTO: QrContentDTO): VcSelectorRequestDTO {
        if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer ")) {
            val errorMessage = "No Bearer token found in Authorization header"
            throw NoAuthorizationFoundException(errorMessage)
        }
        val token = authorizationHeader.substring(7)
        log.info("siopService.processSiopAuthenticationRequest()")
        return siopService.processSiopAuthenticationRequest(qrContentDTO.content, token)
    }

}