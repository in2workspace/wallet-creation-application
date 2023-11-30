package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Schema(description = "This class is used to represent the Credential Offer by Reference using " +
        "credential_offer_uri parameter for a Pre-Authorized Code Flow. " +
        "For more information: https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-sending-credential-offer-by-")
@Builder
public record CredentialOffer(
        @Schema(example = "https://client-issuer.com")
        @JsonProperty("credential_issuer")
        @NotBlank
        String credentialIssuer,
        @Schema(example = "[\"UniversityDegree\"]")
        @JsonProperty("credentials")
        List<?> credentials,
        @Schema(implementation = Grant.class)
        @JsonProperty("grants")
        Grant grant
) {
    @Builder
    public record Grant(
            @JsonProperty("urn:ietf:params:oauth:grant-type:pre-authorized_code") PreAuthorizedCodeGrant preAuthorizedCodeGrant,
            @JsonProperty("authorization_code") AuthorizationCodeGrant authorizationCodeGrant
    ) {
        @Builder
        public record AuthorizationCodeGrant(@JsonProperty("issuer_state") String issuerState) {
        }

        @Builder
        public record PreAuthorizedCodeGrant(
                @JsonProperty("pre-authorized_code")
                String preAuthorizedCode,
                @JsonProperty("user_pin_required")
                boolean userPinRequired
        ) {
        }
    }
}
