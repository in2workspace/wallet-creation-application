package es.in2.walletcreationapplication.config.properties;

import es.in2.walletcreationapplication.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Slf4j
public record OpenApiServerProperties(String url, String description) {

    @ConstructorBinding
    public OpenApiServerProperties(String url, String description) {
        this.url = Utils.isNullOrBlank(url) ? "https://localhost:8080" : url;
        this.description = Utils.isNullOrBlank(description) ? "<server description>" : description;
    }

}
