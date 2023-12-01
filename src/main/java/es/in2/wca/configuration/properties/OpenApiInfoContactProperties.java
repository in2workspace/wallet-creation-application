package es.in2.wca.configuration.properties;

import es.in2.wca.util.Utils;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

public record OpenApiInfoContactProperties(String email, String name, String url) {

    @ConstructorBinding
    public OpenApiInfoContactProperties(String email, String name, String url) {
        this.email = Utils.isNullOrBlank(email) ? "<email of your company>" : email;
        this.name = Utils.isNullOrBlank(name) ? "<name of your company>" : name;
        this.url = Utils.isNullOrBlank(url) ? "<url of your company>" : url;
    }

}
