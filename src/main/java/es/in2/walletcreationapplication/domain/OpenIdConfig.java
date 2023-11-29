package es.in2.walletcreationapplication.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OpenIdConfig {
    private String scope;
    private String responseType;
    private String responseMode;
    private String clientId;
    private String redirectUri;
    private String state;
    private String nonce;
}
