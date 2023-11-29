package es.in2.walletcreationapplication.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CredentialRequestBodyDTO {
    private String format;
    private ProofDTO proof;
}
