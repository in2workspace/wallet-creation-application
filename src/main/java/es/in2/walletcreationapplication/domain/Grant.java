package es.in2.walletcreationapplication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Grant {

    @Schema(example = "1234")
    @NotBlank
    @JsonProperty("pre-authorized_code")
    private String preAuthorizedCode;

    @Schema(example = "true")
    @JsonProperty("user_pin_required")
    private boolean userPinRequired;

}
