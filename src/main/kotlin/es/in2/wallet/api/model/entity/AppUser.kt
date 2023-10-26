package es.in2.wallet.api.model.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "app_users")
data class AppUser(
    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID?,
    @Column(unique = true, nullable = false) val username: String,
    @Column(unique = true, nullable = false) val email: String,
    val password: String
)
