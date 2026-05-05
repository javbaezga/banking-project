package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.Nullable;

@SpringBootApplication
public class BankMainApplication {
    public static void main(@Nullable final String[] args) {
        SpringApplication.run(BankMainApplication.class, args);
    }
}
