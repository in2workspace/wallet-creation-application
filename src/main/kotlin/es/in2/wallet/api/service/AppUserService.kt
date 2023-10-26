package es.in2.wallet.api.service

import es.in2.wallet.api.model.entity.AppUser
import es.in2.wallet.api.model.dto.AppUserRequestDTO
import java.util.*

interface AppUserService {
    fun getUserWithContextAuthentication(): AppUser
    fun registerUser(appUserRequestDTO: AppUserRequestDTO)
    fun getUsers(): List<AppUser>
    fun getUserById(uuid: UUID): Optional<AppUser>
    fun getUserByUsername(username: String): Optional<AppUser>
    fun getUserByEmail(email: String): Optional<AppUser>
    fun checkIfUserExists(username: String): AppUser
}

