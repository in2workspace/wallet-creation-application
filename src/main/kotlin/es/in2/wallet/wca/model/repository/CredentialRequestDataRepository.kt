package es.in2.wallet.wca.model.repository
import es.in2.wallet.wca.model.entity.CredentialRequestData
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CredentialRequestDataRepository : CrudRepository<CredentialRequestData, UUID> {
    fun findAppCredentialRequestDataByIssuerNameAndUserId(issuerName: String, userId: String): Optional<CredentialRequestData>
    @Modifying
    @Query("UPDATE CredentialRequestData c SET c.issuerNonce = :freshNonce WHERE c.issuerName = :issuerName AND c.userId = :userId")
    fun updateIssuerNonceWithNewValueByIssuerNameAndUserId(
            @Param("issuerName") issuerName: String,
            @Param("userId") userId: String,
            @Param("freshNonce") freshNonce: String
    )


}