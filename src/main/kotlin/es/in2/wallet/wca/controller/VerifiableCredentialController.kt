package es.in2.wallet.wca.controller

import es.in2.wallet.wca.model.dto.CredentialRequestDTO
import es.in2.wallet.wca.service.VerifiableCredentialService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/credentials")
class VerifiableCredentialController(
        private val verifiableCredentialService: VerifiableCredentialService
){
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Get a verifiable credential",
            description = "Get a verifiable credential and save it in the personal data space."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Verifiable credential successfully saved."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "403", description = "Access token has expired"),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getVC(@RequestBody credentialRequestDTO: CredentialRequestDTO){
        verifiableCredentialService.getVerifiableCredential(credentialRequestDTO)
    }
}