package es.in2.wallet.wca.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value("\${openapi.server.url}")
    private lateinit var serverUrl: String

    @Value("\${openapi.server.description}")
    private lateinit var serverDescription: String

    @Value("\${openapi.info.title}")
    private lateinit var infoTitle: String

    @Value("\${openapi.info.version}")
    private lateinit var infoVersion: String

    @Value("\${openapi.info.termsOfService}")
    private lateinit var infoTermsOfService: String

    @Value("\${openapi.info.license.name}")
    private lateinit var infoLicenseName: String

    @Value("\${openapi.info.license.url}")
    private lateinit var infoLicenseUrl: String

    @Value("\${openapi.info.contact.email}")
    private lateinit var infoContactEmail: String

    @Value("\${openapi.info.contact.name}")
    private lateinit var infoContactName: String

    @Value("\${openapi.info.contact.url}")
    private lateinit var infoContactUrl: String

    @Value("\${openapi.info.description}")
    private lateinit var infoDescription: String

    @Bean
    fun myOpenAPI(): OpenAPI {
        // Defining servers
        val devServer = getServer()
        // Defining contact info
        val contact = getContact()
        // Defining license info
        val mitLicense = getLicense()
        // Defining application info
        val info = getInfo(contact, mitLicense)
        return OpenAPI().info(info).servers(listOf(devServer))
    }

    private fun getServer(): Server {
        val devServer = Server()
        devServer.url = serverUrl
        devServer.description = serverDescription
        return devServer
    }

    private fun getInfo(contact: Contact, mitLicense: License): Info {
        val info = Info()
        info.title = infoTitle
        info.version = infoVersion
        info.contact = contact
        info.description = infoDescription
        info.termsOfService = infoTermsOfService
        info.license = mitLicense
        return info
    }

    private fun getLicense(): License {
        val mitLicense = License()
        mitLicense.name = infoLicenseName
        mitLicense.url = infoLicenseUrl
        return mitLicense
    }

    private fun getContact(): Contact {
        val contact = Contact()
        contact.email = infoContactEmail
        contact.name = infoContactName
        contact.url = infoContactUrl
        return contact
    }

}