package es.in2.wallet.wca.controller

import es.in2.wallet.wca.model.dto.DidRequestDTO
import es.in2.wallet.wca.model.dto.DidResponseDTO
import es.in2.wallet.wca.service.WalletDidService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "DID Management", description = "Personal Data Space for DID Management API")
@RestController
@RequestMapping("/api/dids")
class DidController(
    private val walletDidService: WalletDidService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create DID",
        description = "Create a new Decentralized Identifier (DID) object in the personal data space.",
        tags = ["DID Management"]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "DID created successfully."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun createDid(@RequestBody didRequestDTO: DidRequestDTO): String {
        walletDidService.createDid(didRequestDTO)
        return "DID created"
    }

    @GetMapping
    @Operation(
        summary = "Get list of DIDs",
        description = "Retrieve a list of Decentralized Identifier (DID) objects associated with the current user.",
        tags = ["DID Management"]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of DIDs retrieved successfully."),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getDidList() : List<DidResponseDTO>{
        return walletDidService.getDidsByUserId()
    }


    // fixme: deleteDid() should use a DidRequestDTO not a DidResponseDTO
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Delete DID",
        description = "Delete the specified Decentralized Identifier (DID) object from the personal data space.",
        tags = ["DID Management"]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "DID deleted successfully."),
        ApiResponse(responseCode = "400", description = "Invalid request."),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun deleteDid(@RequestBody didResponseDTO: DidResponseDTO): String {
        walletDidService.deleteDid(didResponseDTO)
        return "DID deleted"
    }

}