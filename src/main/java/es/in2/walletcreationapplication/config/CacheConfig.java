package es.in2.walletcreationapplication.config;

import es.in2.walletcreationapplication.domain.IssuanceRequestData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    @Bean
    public CacheService cacheService() {
        return new CacheService();
    }

    @Component
    public static class CacheService {
        private final Map<String, IssuanceRequestData> cache = new ConcurrentHashMap<>();

        public Mono<IssuanceRequestData> getIssuanceRequestData(String userId) {
            return Mono.justOrEmpty(cache.get(userId));
        }

        public Mono<IssuanceRequestData> addIssuanceRequestData(String userId, IssuanceRequestData issuanceRequestData) {
            cache.put(userId, issuanceRequestData);
            return Mono.just(issuanceRequestData);
        }
    }
}
