package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CredentialResponseWithAcceptanceToken(
        @JsonProperty("acceptance_token") String acceptanceToken,
        @JsonProperty("c_nonce") String c_nonce,
        @JsonProperty("c_nonce_expires_in") Integer c_nonce_expires_in
) {
}
