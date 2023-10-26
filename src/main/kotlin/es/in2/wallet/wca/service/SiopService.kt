package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.dto.VcSelectorRequestDTO
import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO

interface SiopService {
    fun getSiopAuthenticationRequest(siopAuthenticationRequestUri: String): VcSelectorRequestDTO
    fun processSiopAuthenticationRequest(siopAuthenticationRequest: String): VcSelectorRequestDTO
    fun sendAuthenticationResponse(vcSelectorResponseDTO: VcSelectorResponseDTO, vp: String): String
}