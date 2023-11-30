package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

import java.util.List;

@Builder
public record CredentialIssuerMetadata(
        @JsonProperty("credential_issuer") String credentialIssuer,
        @JsonProperty("credential_endpoint") String credentialEndpoint,
        @JsonProperty("credential_token") String credentialToken,
        @JsonProperty("credentials_supported") List<CredentialsSupported> credentialsSupported
) {
    @Builder
    public record CredentialsSupported(
            @JsonProperty("format") String format,
            @JsonProperty("id") String id,
            @JsonProperty("types") List<String> types,
            @JsonProperty("cryptographic_binding_methods_supported") List<String> cryptographicBindingMethodsSupported,
            @JsonProperty("cryptographic_suites_supported") List<String> cryptographicSuitesSupported,
            @JsonProperty("credentialSubject") JsonNode credentialSubject
    ) {
    }
}
