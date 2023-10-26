package es.in2.wallet.services

import es.in2.wallet.api.model.entity.Issuer
import es.in2.wallet.api.model.repository.IssuerRepository
import es.in2.wallet.api.service.impl.IssuerServiceImpl
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class IssuerServiceImplTest {

    @Mock
    private lateinit var issuerRepository: IssuerRepository

    private lateinit var appIssuerDataServiceImpl: IssuerServiceImpl

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        appIssuerDataServiceImpl = IssuerServiceImpl(issuerRepository)
    }

    @Test
    fun testSaveIssuerData_Success() {
        // Prepare test data
        val issuerName = "issuer123"
        val issuerMetadata = """{"key": "value"}"""

        // Mock behavior of the repository method
        `when`(issuerRepository.findAppIssuerDataByName(issuerName)).thenReturn(Optional.empty())

        // Call the method to be tested
        appIssuerDataServiceImpl.upsertIssuerData(issuerName, issuerMetadata)

        // Verify that the repository method was called
        verify(issuerRepository).findAppIssuerDataByName(issuerName)
        verify(issuerRepository).save(any(Issuer::class.java))
    }

    @Test
    fun testSaveIssuerData_IssuerNameAlreadyExists() {
        // Prepare test data
        val issuerName = "issuer123"
        val issuerMetadata = """{"key": "value"}"""

        val issuer = Issuer(id = UUID.randomUUID(), name = issuerName, metadata = issuerMetadata)
        // Mock behavior of the repository method
        `when`(issuerRepository.findAppIssuerDataByName(issuerName))
            .thenReturn(Optional.of(issuer))
        `when`(issuerRepository.save(issuer)).thenReturn(null)

        // Call the method to be tested
        appIssuerDataServiceImpl.upsertIssuerData(issuerName, issuerMetadata)

        // Verify that the repository method was called
        verify(issuerRepository).findAppIssuerDataByName(issuerName)
        verify(issuerRepository).save(issuer)
        verifyNoMoreInteractions(issuerRepository)
    }

    @Test
    fun testGetIssuerDataByIssuerName_Found() {
        // Prepare test data
        val issuerName = "issuer123"
        val issuerMetadata = """{"key": "value"}"""
        val issuer = Issuer(id = UUID.randomUUID(), name = issuerName, metadata = issuerMetadata)

        // Mock behavior of the repository method
        `when`(issuerRepository.findAppIssuerDataByName(issuerName)).thenReturn(Optional.of(issuer))

        // Call the method to be tested
        val result = appIssuerDataServiceImpl.getIssuerByName(issuerName)

        // Verify the result
        assertTrue(result.isPresent)
        assertEquals(issuer, result.get())
    }

    @Test
    fun testGetIssuerDataByIssuerName_NotFound() {
        // Prepare test data
        val issuerName = "issuer123"

        // Mock behavior of the repository method
        `when`(issuerRepository.findAppIssuerDataByName(issuerName)).thenReturn(Optional.empty())

        // Call the method to be tested
        val result = appIssuerDataServiceImpl.getIssuerByName(issuerName)

        // Verify the result
        assertFalse(result.isPresent)
    }
    @Test
    fun testGetIssuers() {
        val issuer1 = Issuer(id = UUID.randomUUID(), name = "Issuer1", metadata = "Metadata1")
        val issuer2 = Issuer(id = UUID.randomUUID(), name = "Issuer2", metadata = "Metadata2")
        val issuers = listOf(issuer1, issuer2)

        `when`(issuerRepository.findAll()).thenReturn(issuers)

        val result = appIssuerDataServiceImpl.getIssuers()

        assertEquals(2, result.size)
        assertEquals("Issuer1", result[0])
        assertEquals("Issuer2", result[1])
    }
}