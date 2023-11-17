package es.in2.wallet.wca.model.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "issuers")
data class Issuer(

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        val id: UUID?,

        @Column(unique = true, nullable = false)
        val name: String,

        @Column(nullable = false, columnDefinition = "VARCHAR_IGNORECASE")
        val metadata: String

)
