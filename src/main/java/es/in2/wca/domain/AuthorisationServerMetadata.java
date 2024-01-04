package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorisationServerMetadata(
        @JsonProperty("redirect_uris") List<String> redirectUris,
        @JsonProperty("issuer") String issuer,
        @JsonProperty("authorization_endpoint") String authorizationEndpoint,
        @JsonProperty("token_endpoint") String tokenEndpoint,
        @JsonProperty("userinfo_endpoint") String userInfoEndpoint,
        @JsonProperty("presentation_definition_endpoint") String presentationDefinitionEndpoint,
        @JsonProperty("jwks_uri") String jwksUri,
        @JsonProperty("scopes_supported") List<String> scopesSupported,
        @JsonProperty("response_types_supported") List<String> responseTypesSupported,
        @JsonProperty("response_modes_supported") List<String> responseModesSupported,
        @JsonProperty("grant_types_supported") List<String> grantTypesSupported,
        @JsonProperty("subject_types_supported") List<String> subjectTypesSupported,
        @JsonProperty("id_token_signing_alg_values_supported") List<String> idTokenSigningAlgValuesSupported,
        @JsonProperty("request_object_signing_alg_values_supported") List<String> requestObjectSigningAlgValuesSupported,
        @JsonProperty("request_parameter_supported") boolean requestParameterSupported,
        @JsonProperty("request_uri_parameter_supported") boolean requestUriParameterSupported,
        @JsonProperty("token_endpoint_auth_methods_supported") List<String> tokenEndpointAuthMethodsSupported,
        @JsonProperty("request_authentication_methods_supported") AuthenticationMethodsSupported requestAuthenticationMethodsSupported,
        @JsonProperty("vp_formats_supported") VpFormatsSupported vpFormatsSupported,
        @JsonProperty("subject_syntax_types_supported") List<String> subjectSyntaxTypesSupported,
        @JsonProperty("subject_syntax_types_discriminations") List<String> subjectSyntaxTypesDiscriminations,
        @JsonProperty("subject_trust_frameworks_supported") List<String> subjectTrustFrameworksSupported,
        @JsonProperty("id_token_types_supported") List<String> idTokenTypesSupported
) {
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuthenticationMethodsSupported(
            @JsonProperty("authorization_endpoint") List<String> authorizationEndpoint
    ) {
    }
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VpFormatsSupported(
            @JsonProperty("jwt_vp") AlgValuesSupported jwtVp,
            @JsonProperty("jwt_vc") AlgValuesSupported jwtVc
    ) {
        @Builder
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record AlgValuesSupported(
                @JsonProperty("alg_values_supported") List<String> algValuesSupported
        ) {
        }
    }

}
