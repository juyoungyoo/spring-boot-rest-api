package com.juyoung.restapiwithspring.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "app-security")
@Getter @Setter
public class AppSecurityProperties {

    @NotEmpty
    private String adminUsername;
    @NotEmpty
    private String adminPassword;
    @NotEmpty
    private String userUsername;
    @NotEmpty
    private String userPassword;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;
}
