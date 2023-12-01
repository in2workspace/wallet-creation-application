package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

/**
 * Credential EBSI Format is used to represent the 'credentials' attribute of the Credential Offer issued by EBSI.
 * @param format
 * @param types
 * @param trustFramework
 */
@Builder
public record CredentialEbsiFormat(
        @JsonProperty("format") String format,
        @JsonProperty("types") List<String> types,
        @JsonProperty("trust_framework") TrustFramework trustFramework
) {
    @Builder
    public record TrustFramework(
            @JsonProperty("name")String name,
            @JsonProperty("type")String type,
            @JsonProperty("uri")String uri
    ) {
    }
}