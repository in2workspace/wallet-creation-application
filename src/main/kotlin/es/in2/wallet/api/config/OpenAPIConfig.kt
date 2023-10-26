package es.in2.wallet.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig(
    @Value("\${in2.openapi.local-url}") private val localUrl: String,
    @Value("\${in2.openapi.dev-url}") private val devUrl: String,
    @Value("\${app.url.api}") private val apiUrl: String,
) {

    @Bean
    fun myOpenAPI(): OpenAPI {
        // defining servers info
        val localServer = Server()
        localServer.url = localUrl
        localServer.description = "Server URL in Local environment"
        val devServer = Server()
        devServer.url = devUrl
        devServer.description = "Server URL in Dev environment"
        // defining contact info
        val contact = Contact()
        contact.email = "oriol.canades@in2.es"
        contact.name = "IN2"
        contact.url = "https://in2.es"
        // defining license info
        val mitLicense: License = License().name("MIT License").url("https://choosealicense.com/licenses/mit/")
        // defining application info
        val info: Info = Info()
            .title("IN2 Wallet API")
            .version("1.5")
            .contact(contact)
            .description("This API exposes endpoints to manage the IN2 Wallet application.")
            .termsOfService("$apiUrl/terms-of-service")
            .license(mitLicense)
        return OpenAPI().info(info).servers(listOf(localServer, devServer))
    }

}