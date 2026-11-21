package pl.grupanet.eakademik.configs;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.grupanet.eakademik.controllers.ApiVersions;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(@NonNull final PathMatchConfigurer configurer) {
        for (ApiVersions value : ApiVersions.values()) {
            configurer.addPathPrefix(
                    ApiVersions.V1.getPath(),
                    HandlerTypePredicate.forBasePackage("pl.grupanet.eakademik.controllers." + value.toString())
            );
        }
    }
}