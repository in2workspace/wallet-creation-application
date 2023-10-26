package es.in2.wallet.api.controller

import es.in2.wallet.api.model.entity.AppUser
import es.in2.wallet.api.model.dto.AppUserRequestDTO
import es.in2.wallet.api.service.AppUserService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.websocket.server.PathParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Users", description = "Users management API")
@RestController
@RequestMapping("/api/users")
class AppUserController(
    private val appUserService: AppUserService
) {

    private val log: Logger = LoggerFactory.getLogger(AppUserController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody appUserRequestDTO: AppUserRequestDTO) {
        log.debug("AppUserController.registerUser()")
        appUserService.registerUser(appUserRequestDTO)
    }

    // fixme: this method should return List<AppUserResponseDTO>
    @GetMapping
    fun getAllUsers(): List<AppUser> {
        log.debug("AppUserController.getAllUsers()")
        return appUserService.getUsers()
    }

    // fixme: this method should return an AppUserResponseDTO
    @GetMapping("/uuid")
    fun getUserByUUID(@PathParam("uuid") uuid: String): Optional<AppUser> {
        log.debug("AppUserController.getUserByUUID()")
        return appUserService.getUserById(UUID.fromString(uuid))
    }

    // fixme: this method should return an AppUserResponseDTO
    @GetMapping("/username")
    fun getUserByUsername(@PathParam("username") username: String): Optional<AppUser> {
        log.debug("AppUserController.getUserByUsername()")
        return appUserService.getUserByUsername(username)
    }

}