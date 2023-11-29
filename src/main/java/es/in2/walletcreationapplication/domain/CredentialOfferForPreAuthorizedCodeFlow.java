package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "This class is used to represent the Credential Offer by Reference using " +
        "credential_offer_uri parameter for a Pre-Authorized Code Flow. " +
        "For more information: https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-sending-credential-offer-by-")
public class CredentialOfferForPreAuthorizedCodeFlow {

    @Schema(example = "https://credential-issuer.example.com")
    @NotBlank
    @JsonProperty("credential_issuer")
    private String credentialIssuer;

    @Schema(example = "[\"UniversityDegree\"]")
    @NotBlank
    @JsonProperty("credentials")
    private List<String> credentials;

    //TODO: The key should not be string but a specific keyword like "urn:ietf:params:oauth:grant-type:pre-authorized_code"
    @Schema(implementation = Grant.class)
    @JsonProperty("grants")
    private Map<String, Grant> grants;

}
