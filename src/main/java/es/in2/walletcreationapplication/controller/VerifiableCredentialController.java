package es.in2.walletcreationapplication.controller;

import es.in2.walletcreationapplication.domain.QrContentDTO;
import es.in2.walletcreationapplication.service.IssuanceFacadeService;
import es.in2.walletcreationapplication.service.VerifiableCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import static es.in2.walletcreationapplication.util.Utils.*;
@RestController
@RequestMapping("/api/credentials")
@Slf4j
@RequiredArgsConstructor
public class VerifiableCredentialController {
    private final VerifiableCredentialService verifiableCredentialService;
    private final IssuanceFacadeService issuanceFacadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Get a verifiable credential",
            description = "Get a verifiable credential and save it in the personal data space."
    )
    @ApiResponse(responseCode = "201", description = "Verifiable credential successfully saved.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "403", description = "Access token has expired.")
    public Mono<Void> getVC(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        log.debug("VerifiableCredentialController.getVC");
        return issuanceFacadeService.getAndSaveVC(authorizationHeader);
    }

    @PostMapping("/issuer-metadata")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Get Credential Issuer Metadata",
            description = "Get the Issuer Metadata of the credential"
    )
    @ApiResponse(responseCode = "201", description = "Credential data successfully saved.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "403", description = "Access token has expired.")
    public Mono<Void> getCredentialIssuerMetadata(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody QrContentDTO qrContentDTO){
        log.debug("VerifiableCredentialController.getVC");
        return getUserIdFromToken(authorizationHeader)
                .flatMap(userId -> verifiableCredentialService.getCredentialIssuerMetadata(qrContentDTO.getContent(), userId))
                .then();
    }
}
