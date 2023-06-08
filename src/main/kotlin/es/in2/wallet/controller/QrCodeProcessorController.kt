package es.in2.wallet.controller

import es.in2.wallet.model.dto.QrContentDTO
import es.in2.wallet.service.QrCodeProcessorService
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "QR Codes", description = "QR code management API")
@RestController
@RequestMapping("/api/execute-content")
class QrCodeProcessorController(
    private val qrCodeProcessorService: QrCodeProcessorService
) {

    private val log: Logger = LogManager.getLogger(QrCodeProcessorController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun executeQrContent(@RequestBody qrContentDTO: QrContentDTO): Any {
        log.info("QrCodeProcessorController.executeQrContent()")
        return qrCodeProcessorService.processQrContent(qrContentDTO.content)
    }

}
