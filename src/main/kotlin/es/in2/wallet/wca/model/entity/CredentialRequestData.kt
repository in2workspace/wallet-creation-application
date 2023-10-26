package es.in2.wallet.wca.model.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "credential_request_data")
data class CredentialRequestData(

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        val id: UUID?,

        // fixme: this should be unique and issuerId
        @Column(nullable = false)
        val issuerName: String,

        @Column(nullable = false)
        val userId: String,

        @Column(length = 50)
        val issuerNonce: String,

        @Column(length = 2000,unique = true)
        val issuerAccessToken: String
)

