package com.banking.infrastructure.output.repository;

import com.banking.infrastructure.output.repository.entity.TransactionEntity;
import com.banking.infrastructure.output.repository.projection.BankStatementProjection;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface BankStatementJpaRepository extends Repository<TransactionEntity, Long> {
    @Query("""
        SELECT
            trx.id AS id,
            trx.date AS date,
            cus.fullName AS customer,
            acc.number AS accountNumber,
            trx.type AS type,
            acc.initialBalance AS initialBalance,
            trx.status AS status,
            trx.value AS value,
            trx.balance AS balance
        FROM TransactionEntity trx
        JOIN trx.account acc
        JOIN acc.customer cus
        WHERE cus.id = :customerId
            AND trx.date BETWEEN :startDate AND :endDate
        """)
    Page<BankStatementProjection> getBankStatement(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
}
