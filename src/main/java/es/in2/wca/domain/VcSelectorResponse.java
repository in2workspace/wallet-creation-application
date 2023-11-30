package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record VcSelectorResponse(
        @JsonProperty("redirectUri") String redirectUri,
        @JsonProperty("state") String state,
        @JsonProperty("selectedVcList") List<CredentialsBasicInfo> selectedVcList
) {
}
