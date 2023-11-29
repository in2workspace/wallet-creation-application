package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsSupportedParameter {
    @JsonProperty("format")
    private String format;
    @JsonProperty("id")
    private String id;
    @JsonProperty("types")
    private List<String> types;
    @JsonProperty("cryptographic_binding_methods_supported")
    private List<String> cryptographicBindingMethodsSupported;
    @JsonProperty("cryptographic_suites_supported")
    private List<String> cryptographicSuitesSupported;
    @JsonProperty("credentialSubject")
    private JsonNode credentialSubject;
}
