package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VCRequestDTO {
    @JsonProperty("credential")
    private String credential;
}
