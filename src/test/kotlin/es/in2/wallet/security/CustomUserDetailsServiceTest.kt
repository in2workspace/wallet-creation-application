package es.in2.wallet.security

import es.in2.wallet.api.security.CustomUserDetailsService
import es.in2.wallet.api.model.entity.AppUser
import es.in2.wallet.api.service.AppUserService
import es.in2.wallet.api.util.USER_ROLE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Mock
    private lateinit var appUserService: AppUserService

    private lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        customUserDetailsService = CustomUserDetailsService(appUserService)
    }

    @Test
    fun testLoadUserByUsername() {
        val username = "testUser"
        val password = "testPassword"
        val userFound = AppUser(UUID.randomUUID(), username, "testEmail", password)
        `when`(appUserService.checkIfUserExists(username)).thenReturn(userFound)
        val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(username)
        assertEquals(username, userDetails.username)
        assertEquals(password, userDetails.password)
        val authorities: Collection<GrantedAuthority> = userDetails.authorities
        assertEquals(1, authorities.size)
        assertEquals(USER_ROLE, authorities.iterator().next().authority)
    }

}