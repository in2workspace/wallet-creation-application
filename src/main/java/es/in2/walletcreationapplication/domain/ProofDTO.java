package es.in2.walletcreationapplication.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ProofDTO {
    private String proofType;
    private String jwt;
}
