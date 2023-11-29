package es.in2.walletcreationapplication.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class IssuanceRequestData {
    private String nonce;
    private String accessToken;
    private CredentialIssuerMetadata metadata;
}
