package es.in2.wca.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wallet-data")
public record WalletDataProperties(String url) {
}
