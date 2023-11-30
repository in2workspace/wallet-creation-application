package es.in2.wca.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record QrContent(@JsonProperty("qr_content") String content) {
}
