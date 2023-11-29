package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VcSelectorResponseDTO {
    @JsonProperty("redirectUri")
    private String redirectUri;

    @JsonProperty("state")
    private String state;

    @JsonProperty("selectedVcList")
    private List<VcBasicDataDTO> selectedVcList;

}
