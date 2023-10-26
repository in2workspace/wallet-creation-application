package es.in2.wallet.api.service.impl

import es.in2.wallet.api.model.entity.AppUser
import es.in2.wallet.api.model.dto.AppUserRequestDTO
import es.in2.wallet.api.model.repository.AppUserRepository
import es.in2.wallet.api.service.AppUserService
import es.in2.wallet.api.exception.EmailAlreadyExistsException
import es.in2.wallet.api.exception.UsernameAlreadyExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppUserServiceImpl(
    private val appUserRepository: AppUserRepository
) : AppUserService {

    private val log: Logger = LoggerFactory.getLogger(AppUserServiceImpl::class.java)

    override fun checkIfUserExists(username: String): AppUser {
        val userFound = getUserByUsername(username)
        if (userFound.isPresent) {
            return userFound.get()
        } else {
            throw NoSuchElementException("The username $username does not exist.")
        }
    }

    override fun getUserWithContextAuthentication(): AppUser {
        log.info("AppUserServiceImpl.getUserWithContextAuthentication()")
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return getUserByUsername(authentication.name).get()
    }

    override fun registerUser(appUserRequestDTO: AppUserRequestDTO) {
        log.info("AppUserServiceImpl.registerUser()")
        checkIfUsernameAlreadyExist(appUserRequestDTO)
        checkIfEmailAlreadyExist(appUserRequestDTO)
        val appUser = AppUser(
            id = UUID.randomUUID(),
            username = appUserRequestDTO.username,
            email = appUserRequestDTO.email,
            password = BCryptPasswordEncoder().encode(appUserRequestDTO.password)
        )
        log.info(appUser.id.toString())
        saveUser(appUser)
    }

    override fun getUsers(): List<AppUser> {
        log.info("AppUserServiceImpl.getUsers()")
        return listOf(appUserRepository.findAll()).flatten()
    }

    override fun getUserById(uuid: UUID): Optional<AppUser> {
        log.info("AppUserServiceImpl.getUserById()")
        return appUserRepository.findById(uuid)
    }

    override fun getUserByUsername(username: String): Optional<AppUser> {
        log.info("AppUserServiceImpl.getUserByUsername()")
        return appUserRepository.findAppUserByUsername(username)
    }

    override fun getUserByEmail(email: String): Optional<AppUser> {
        log.info("AppUserServiceImpl.getUserByEmail()")
        return appUserRepository.findAppUserByEmail(email)
    }

    private fun saveUser(appUser: AppUser) {
        log.info("AppUserServiceImpl.saveUser()")
        appUserRepository.save(appUser)
    }

    private fun checkIfUsernameAlreadyExist(appUserRequestDTO: AppUserRequestDTO) {
        log.info("AppUserServiceImpl.checkIfUsernameAlreadyExist()")
        if (getUserByUsername(appUserRequestDTO.username).isPresent) {
            throw UsernameAlreadyExistsException("Username already exists: ${appUserRequestDTO.username}")
        }
    }

    private fun checkIfEmailAlreadyExist(appUserRequestDTO: AppUserRequestDTO) {
        log.info("AppUserServiceImpl.checkIfEmailAlreadyExist()")
        if (getUserByEmail(appUserRequestDTO.email).isPresent) {
            throw EmailAlreadyExistsException("Email already exists: ${appUserRequestDTO.email}")
        }
    }

}