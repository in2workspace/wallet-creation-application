package es.in2.wallet.services

import es.in2.wallet.api.exception.EmailAlreadyExistsException
import es.in2.wallet.api.exception.UsernameAlreadyExistsException
import es.in2.wallet.api.model.entity.AppUser
import es.in2.wallet.api.model.dto.AppUserRequestDTO
import es.in2.wallet.api.model.repository.AppUserRepository
import es.in2.wallet.api.service.impl.AppUserServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class AppUserServiceImplTest {

    @Mock
    private lateinit var appUserRepository: AppUserRepository

    private lateinit var appUserServiceImpl: AppUserServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        appUserServiceImpl = AppUserServiceImpl(appUserRepository)
    }

    @Test
    fun testRegisterUser() {
        val appUserRequestDTO = AppUserRequestDTO(username = "jdoe", email = "jdoe@example.com", password = "1234")
        val appUser = AppUser(UUID.randomUUID(), "jdoe", "jdoe@example.com", "hashedPassword")
        `when`(appUserRepository.findAppUserByUsername(appUserRequestDTO.username)).thenReturn(Optional.empty())
        `when`(appUserRepository.findAppUserByEmail(appUserRequestDTO.email)).thenReturn(Optional.empty())
        `when`(appUserRepository.save(any(AppUser::class.java))).thenReturn(appUser)
        appUserServiceImpl.registerUser(appUserRequestDTO)
        verify(appUserRepository).findAppUserByUsername(appUserRequestDTO.username)
        verify(appUserRepository).findAppUserByEmail(appUserRequestDTO.email)
        verify(appUserRepository).save(any(AppUser::class.java))
    }

    @Test
    fun testRegisterUser_UsernameAlreadyExists() {
        val appUserRequestDTO = AppUserRequestDTO(username = "jdoe", email = "jdoe@example.com", password = "1234")
        val existingUser = AppUser(UUID.randomUUID(), "jdoe", "jdoe@example.com", "hashedPassword")
        `when`(appUserRepository.findAppUserByUsername(appUserRequestDTO.username)).thenReturn(Optional.of(existingUser))
        try {
            appUserServiceImpl.registerUser(appUserRequestDTO)
        } catch (e: UsernameAlreadyExistsException) {
            assertThat(e.message).isEqualTo("Username already exists: ${appUserRequestDTO.username}")
        }
        verify(appUserRepository).findAppUserByUsername(appUserRequestDTO.username)
        verifyNoMoreInteractions(appUserRepository)
    }

    @Test
    fun testRegisterUser_EmailAlreadyExists() {
        val appUserRequestDTO = AppUserRequestDTO(username = "jdoe", email = "jdoe@example.com", password = "1234")
        val existingUser = AppUser(UUID.randomUUID(), "jdoe", "jdoe@example.com", "hashedPassword")
        `when`(appUserRepository.findAppUserByUsername(appUserRequestDTO.username)).thenReturn(Optional.empty())
        `when`(appUserRepository.findAppUserByEmail(appUserRequestDTO.email)).thenReturn(Optional.of(existingUser))
        try {
            appUserServiceImpl.registerUser(appUserRequestDTO)
        } catch (e: EmailAlreadyExistsException) {
            assertThat(e.message).isEqualTo("Email already exists: ${appUserRequestDTO.email}")
        }
        verify(appUserRepository).findAppUserByUsername(appUserRequestDTO.username)
        verify(appUserRepository).findAppUserByEmail(appUserRequestDTO.email)
        verifyNoMoreInteractions(appUserRepository)
    }

    @Test
    fun testGetUsers() {
        // Mock the behavior of the appUserRepository
        val user1 = AppUser(UUID.randomUUID(), "user1", "user1@example.com", "password1")
        val user2 = AppUser(UUID.randomUUID(), "user2", "user2@example.com", "password2")
        val userList = listOf(user1, user2)
        `when`(appUserRepository.findAll()).thenReturn(userList)
        // Call the getUsers method
        val result = appUserServiceImpl.getUsers()
        // Verify the result
        assertEquals(userList, result)
    }

    @Test
    fun testGetUserById() {
        // Mock the behavior of the appUserRepository
        val userId = UUID.randomUUID()
        val user = AppUser(userId, "user", "user@example.com", "password")
        `when`(appUserRepository.findById(userId)).thenReturn(Optional.of(user))
        // Call the getUserById method
        val result = appUserServiceImpl.getUserById(userId)
        // Verify the result
        Assertions.assertTrue(result.isPresent)
        assertEquals(user, result.get())
    }


    @Test
    fun testCheckIfUserExists_UserFound() {
        val username = "testuser"
        val userFound = AppUser(UUID.randomUUID(), username, "testEmail", "testPassword")
        `when`(appUserRepository.findAppUserByUsername(username)).thenReturn(Optional.of(userFound))
        val result: AppUser = appUserServiceImpl.checkIfUserExists(username)
        assertEquals(userFound, result)
    }

    @Test
    fun testCheckIfUserExists_UserNotFound() {
        val username = "nonexistentuser"
        `when`(appUserRepository.findAppUserByUsername(username)).thenReturn(Optional.empty())
        val exception: NoSuchElementException = assertThrows(NoSuchElementException::class.java) {
            appUserServiceImpl.checkIfUserExists(username)
        }
        assertEquals("The username $username does not exist.", exception.message)
    }

}
