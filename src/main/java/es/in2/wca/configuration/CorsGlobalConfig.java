package es.in2.wca.configuration;

import es.in2.wca.configuration.properties.WalletDrivingApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import static es.in2.wca.util.Utils.ALLOWED_METHODS;
import static es.in2.wca.util.Utils.GLOBAL_ENDPOINTS_API;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class CorsGlobalConfig implements WebFluxConfigurer {
    private final WalletDrivingApplicationProperties walletDrivingApplicationProperties;
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping(GLOBAL_ENDPOINTS_API)
                .allowedOrigins(walletDrivingApplicationProperties.url())
                .allowedMethods(ALLOWED_METHODS)
                .maxAge(3600);
    }
}
