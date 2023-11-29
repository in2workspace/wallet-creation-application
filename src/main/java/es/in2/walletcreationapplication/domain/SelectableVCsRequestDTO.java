package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SelectableVCsRequestDTO {
    @JsonProperty("vcTypes")
    private List<String> vcTypes;
}
