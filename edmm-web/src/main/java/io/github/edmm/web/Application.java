package io.github.edmm.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@OpenAPIDefinition(info = @Info(
        title = "EDMM Transformation API",
        contact = @Contact(name = "Michael Wurster",
                email = "michael.wurster@iaas.uni-stuttgart.de",
                url = "https://www.iaas.uni-stuttgart.de/institut/team/Wurster"),
        license = @License(name = "Apache License 2.0", url = "http://www.apache.org/licenses"),
        version = "1.0"
))
@SpringBootApplication(scanBasePackages = "io.github.edmm")
@ImportResource( {"classpath*:pluginContext.xml"})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
