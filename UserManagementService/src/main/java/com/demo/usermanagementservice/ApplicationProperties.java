package com.demo.usermanagementservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("user-management-service")
public class ApplicationProperties {
    private String emailFrom;
    private String emailServiceAuthHeader;

}
