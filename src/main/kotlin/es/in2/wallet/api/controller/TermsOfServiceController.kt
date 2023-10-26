package es.in2.wallet.api.controller

import es.in2.wallet.api.model.dto.TermsOfService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/terms-of-service")
class TermsOfServiceController {

    @GetMapping
    fun getTermsOfService(): TermsOfService {
        return TermsOfService()
    }

}