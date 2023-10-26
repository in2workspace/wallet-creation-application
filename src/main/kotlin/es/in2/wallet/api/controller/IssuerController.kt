package es.in2.wallet.api.controller

import es.in2.wallet.api.service.IssuerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@Tag(name = "Issuers", description = "Issuers management API")
@RestController
@RequestMapping("/api/issuers")
class IssuerController(
        private val issuerService: IssuerService
) {

    private val log: Logger = LoggerFactory.getLogger(AppUserController::class.java)

    // todo: change to IssuerResponseDTO
    @GetMapping
    @Operation(
            summary = "Get list of Issuers",
            description = "Retrieve a list of Issuers names.",
            tags = ["Issuer Management"]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of Issuers retrieved successfully."),
        ApiResponse(responseCode = "500", description = "Internal server error.")
    ])
    fun getAllIssuers(): List<String> {
        log.debug("AppIssuerController.getAllIssuers()")
        return issuerService.getIssuers()
    }

}