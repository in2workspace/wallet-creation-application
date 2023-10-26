package es.in2.wallet.api.model.entity

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

        //Do not remove the columnDefinition annotation to prevent MySQL data truncation
        @Column(nullable = false, columnDefinition = "json")
        val metadata: String

)
