package com.petadoption.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A custom implementation of the {@link InfoContributor} interface
 * that provides application-specific information to be included
 * in the Spring Boot Actuator info endpoint.

 * This contributor adds details about the application such as its name,
 * description, version, and the author of the service.
 */
@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", Map.of(
                "name", "Pet Adoption API",
                "description", "Pet adoption system",
                "version", "2024.1"
        ));
        builder.withDetail("author", "Emily Andrea Chacon Guerrero");
    }
}
