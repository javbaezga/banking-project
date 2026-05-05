package com.banking.infrastructure.output.repository;

import com.banking.infrastructure.output.repository.entity.AccountEntity;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long>,
    JpaSpecificationExecutor<AccountEntity> {
    Optional<AccountEntity> findByNumber(String number);

    Optional<List<AccountEntity>> findByCustomerId(Long customerId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE AccountEntity
        SET
            dailyBalance = :dailyBalance,
            dailyBalanceResetDate = :resetDate
        WHERE dailyBalanceResetDate IS NULL
            OR dailyBalanceResetDate < :resetDate
        """
    )
    int resetDailyBalances(@Param("dailyBalance") BigDecimal dailyBalance, @Param("resetDate") LocalDate resetDate);
}
