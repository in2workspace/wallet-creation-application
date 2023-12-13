package es.in2.wca.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Optional;

/**
 * WalletCryptoProperties
 *
 * @param url - wallet crypto url
 */
@ConfigurationProperties(prefix = "wallet-crypto")
public record WalletCryptoProperties(String url) {

    @ConstructorBinding
    public WalletCryptoProperties(String url) {
        this.url = Optional.ofNullable(url).orElse("http://localhost:8087");
    }

}

