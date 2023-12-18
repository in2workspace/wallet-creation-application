package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
public record SignRequest(
        @JsonProperty("did") String did,
        @JsonProperty("document") JsonNode document,
        @JsonProperty("document_type") String documentType
) {
}