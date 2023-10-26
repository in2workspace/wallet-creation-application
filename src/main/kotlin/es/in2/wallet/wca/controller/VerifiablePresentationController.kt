package es.in2.wallet.wca.controller

import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO
import es.in2.wallet.wca.service.SiopService
import es.in2.wallet.wca.service.VerifiablePresentationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Verifiable Presentations", description = "Verifiable Presentation management API")
@RestController
// todo: change this path to /api/presentations
@RequestMapping("/api/vp")
class VerifiablePresentationController(
    private val verifiablePresentationService: VerifiablePresentationService,
    private val siopService: SiopService
) {

    private val log: Logger = LogManager.getLogger(VerifiablePresentationController::class.java)

    @Operation(summary = "Create a Verifiable Presentation with the Verifiable Credentials attached.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The Verifiable Credential was created and send successfully."
            )
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createVerifiablePresentation(@RequestBody vcSelectorResponseDTO: VcSelectorResponseDTO): String {
        // create a verifiable presentation
        log.info("Creating Verifiable Presentation...")
        val verifiablePresentation = verifiablePresentationService.createVerifiablePresentation(vcSelectorResponseDTO)
        // send the verifiable presentation to the dome backend
        log.info("Sending Authentication Response using RedirectUri...")
        return siopService.sendAuthenticationResponse(vcSelectorResponseDTO, verifiablePresentation)
    }

}