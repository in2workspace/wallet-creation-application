package es.in2.wca.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * WalletDataProperties
 *
 * @param url - wallet data url
 */
@ConfigurationProperties(prefix = "wallet-data")
public record WalletDataProperties(String url) {

    @ConstructorBinding
    public WalletDataProperties(String url) {
        this.url = Optional.ofNullable(url).orElse("http://localhost:8086");
    }

}
