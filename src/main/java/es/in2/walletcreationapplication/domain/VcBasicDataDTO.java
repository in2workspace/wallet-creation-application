package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VcBasicDataDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("vcType")
    private List<String> vcType;

    @JsonProperty("credentialSubject")
    private JsonNode credentialSubject;
}
