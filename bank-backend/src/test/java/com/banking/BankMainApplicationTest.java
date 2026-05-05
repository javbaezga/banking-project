package com.banking;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = {BankMainApplication.class})
@ExtendWith(MockitoExtension.class)
class BankMainApplicationTest {
    @MockitoBean
    private ConfigurableApplicationContext configurableApplicationContext;

    @Test
    void shouldReturnBankMainApplicationWhenCreatingInstanceOfBankMainApplication() {
        assertDoesNotThrow(BankMainApplication::new);
    }

    @Test
    void givenApplicationArgumentsWhenRunApplicationThenApplicationExecutionIsVerified() {
        try (final var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            final var applicationArguments = new String[] {};
            mockedSpringApplication.when(() -> SpringApplication.run(BankMainApplication.class, applicationArguments))
                .thenReturn(configurableApplicationContext);
            BankMainApplication.main(applicationArguments);
            mockedSpringApplication.verify(() -> SpringApplication.run(BankMainApplication.class, applicationArguments));
        }
    }
}
