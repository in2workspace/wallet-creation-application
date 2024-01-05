package es.in2.wca.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * AuthServerProperties
 *
 * @param domain - auth-server url
 * @param tokenEndpoint - token Endpoint
 */
@ConfigurationProperties(prefix = "auth-server")
public record AuthServerProperties(String domain, String tokenEndpoint) {

    @ConstructorBinding
    public AuthServerProperties(String domain, String tokenEndpoint) {
        this.domain = Optional.ofNullable(domain).orElse("https://issuerkeycloak.demo.in2.es/realms/EAAProvider");
        this.tokenEndpoint = Optional.ofNullable(tokenEndpoint).orElse("https://issuerkeycloak.demo.in2.es/realms/EAAProvider/verifiable-credential/did:key:z6MkqmaCT2JqdUtLeKah7tEVfNXtDXtQyj4yxEgV11Y5CqUa/token");
    }

}
