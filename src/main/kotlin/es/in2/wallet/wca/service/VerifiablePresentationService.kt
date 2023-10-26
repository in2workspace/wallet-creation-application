package es.in2.wallet.wca.service

import es.in2.wallet.wca.model.dto.VcSelectorResponseDTO

fun interface VerifiablePresentationService {
    fun createVerifiablePresentation(vcSelectorResponseDTO: VcSelectorResponseDTO): String
}

