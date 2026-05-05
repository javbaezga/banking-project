package com.banking.infrastructure.output.adapter.configuration;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "account")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountConfigurationProperties {
    Account account;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Account {
        BigDecimal dailyQuota;
    }
}
