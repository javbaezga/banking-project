package com.banking.infrastructure.input.adapter.job.impl;

import com.banking.application.input.port.AccountService;
import com.banking.infrastructure.input.adapter.job.configuration.AccountJobConfigurationProperties;
import com.banking.infrastructure.input.adapter.job.mapper.AccountJobMapper;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountJob {
    AccountService accountService;
    AccountJobMapper accountJobMapper;
    AccountJobConfigurationProperties accountJobConfigurationProperties;

    @PostConstruct
    private void resetDailyBalancesOnStartup() {
        resetDailyBalances();
    }

    @Scheduled(
        cron = "#{accountJobConfigurationProperties.scheduling.cron}",
        zone = "#{accountJobConfigurationProperties.scheduling.zone}"
    )
    @Async
    public void resetDailyBalances() {
        log.info("|-> Resetting daily balances");
        Mono.fromCallable(() -> accountJobMapper.toResetDailyBalancesInput(accountJobConfigurationProperties))
            .flatMap(accountService::resetDailyBalances)
            .doOnSuccess(resetDailyBalancesOutput -> log.info("<-| Daily balances were reset: output={}",
                resetDailyBalancesOutput))
            .doOnError(error -> log.error("<-| Error resetting daily balances: error={}", error.getMessage(), error))
            .subscribe();
    }
}
