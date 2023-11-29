package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class VcSelectorRequestDTO {
    @JsonProperty("redirectUri")
    private String redirectUri;

    @JsonProperty("state")
    private String state;

    @JsonProperty("selectableVcList")
    private List<VcBasicDataDTO> selectableVcList;
}
