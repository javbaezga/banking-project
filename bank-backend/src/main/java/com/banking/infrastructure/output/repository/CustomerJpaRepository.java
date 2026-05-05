package com.banking.infrastructure.output.repository;

import com.banking.infrastructure.output.repository.entity.CustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long>,
    JpaSpecificationExecutor<CustomerEntity> {
    Optional<CustomerEntity> findByIdNumber(String idNumber);
}
