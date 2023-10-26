package es.in2.wallet.api.security

import es.in2.wallet.api.service.AppUserService
import es.in2.wallet.api.util.USER_ROLE
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val appUserService: AppUserService
) : UserDetailsService {

    private val log: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        log.debug("CustomUserDetailsService.loadUserByUsername()")
        val userFound = appUserService.checkIfUserExists(username)
        val authorities = ArrayList<GrantedAuthority>()
        // For now, we do not have roles, but we set USER as default
        authorities.add(SimpleGrantedAuthority(USER_ROLE))
        return User(userFound.username, userFound.password, authorities)
    }

}