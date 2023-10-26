package es.in2.wallet.integration.orion.service

import es.in2.wallet.wca.model.entity.DidMethods
import es.in2.wallet.wca.model.dto.DidResponseDTO
import es.in2.wallet.wca.model.dto.VcBasicDataDTO


interface OrionService {

    fun saveVC(vcJwt: String)
    fun getUserVCsInJson(): MutableList<VcBasicDataDTO>
    fun getVerifiableCredentialByIdAndFormat(id: String, format: String): String
    fun deleteVerifiableCredential(id: String)
    fun getSelectableVCsByVcTypeList(vcTypeList: List<String>): List<VcBasicDataDTO>
    fun deleteVCs()
    fun saveDid(did: String, didMethod: DidMethods)
    fun getDidsByUserId(): MutableList<DidResponseDTO>
    fun deleteSelectedDid(didResponseDTO: DidResponseDTO)



}




