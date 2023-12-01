package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.List;

@Builder
public record CredentialsBasicInfo(
        @JsonProperty("id") String id,
        @JsonProperty("vcType") List<String> vcType,
        @JsonProperty("credentialSubject") JsonNode credentialSubject
) {
}
