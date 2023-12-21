package es.in2.wca.configuration;

import es.in2.wca.configuration.properties.OpenApiProperties;
import es.in2.wca.configuration.properties.WalletCryptoProperties;
import es.in2.wca.configuration.properties.WalletDataProperties;
import es.in2.wca.configuration.properties.WalletDrivingApplicationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfigs {

    private final OpenApiProperties openApiProperties;
    private final WalletDataProperties walletDataProperties;
    private final WalletDrivingApplicationProperties walletDrivingApplicationProperties;
    private final WalletCryptoProperties walletCryptoProperties;

    @PostConstruct
    void init() {
        String prefixMessage = " > {}";
        log.info("Configurations uploaded: ");
        log.info(prefixMessage, openApiProperties.server());
        log.info(prefixMessage, openApiProperties.info());
        log.info(prefixMessage, walletDataProperties);
        log.info(prefixMessage, walletDrivingApplicationProperties);
        log.info(prefixMessage, walletCryptoProperties);
    }

}