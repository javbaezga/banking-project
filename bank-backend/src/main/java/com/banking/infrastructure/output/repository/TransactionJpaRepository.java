package com.banking.infrastructure.output.repository;

import com.banking.infrastructure.output.repository.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long>,
    JpaSpecificationExecutor<TransactionEntity> {
}
