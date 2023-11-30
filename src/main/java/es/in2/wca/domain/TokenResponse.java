package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("c_nonce") String cNonce,
        @JsonProperty("c_nonce_expires_in") String cNonceExpiresIn
) {
}
