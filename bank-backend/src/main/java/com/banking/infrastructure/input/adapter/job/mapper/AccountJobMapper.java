package com.banking.infrastructure.input.adapter.job.mapper;

import com.banking.domain.ResetDailyBalancesInput;
import com.banking.infrastructure.input.adapter.job.configuration.AccountJobConfigurationProperties;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface AccountJobMapper {
    @Mapping(target = "dailyBalance", source = "account.dailyQuota")
    @Mapping(target = "resetDate", expression = "java(LocalDate.now())")
    ResetDailyBalancesInput toResetDailyBalancesInput(
        AccountJobConfigurationProperties accountJobConfigurationProperties);
}
