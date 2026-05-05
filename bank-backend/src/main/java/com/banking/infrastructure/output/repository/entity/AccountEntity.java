package com.banking.infrastructure.output.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "account",
    indexes = {
        @Index(name = "NUMBER_INDEX", columnList = "number", unique = true),
        @Index(name = "DAILY_BALANCE_RESET_DATE_INDEX", columnList = "daily_balance_reset_date")
    }
)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountEntity {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_ACCOUNT_TO_CUSTOMER"))
    @ManyToOne(fetch = FetchType.EAGER)
    CustomerEntity customer;
    @Column(name = "number", nullable = false, length = 6)
    String number;
    @Column(name = "type", nullable = false, length = 1)
    String type;
    @Column(name = "initial_balance", nullable = false, precision = 14, scale = 2)
    BigDecimal initialBalance;
    @Column(name = "balance", nullable = false, precision = 14, scale = 2)
    BigDecimal balance;
    @Column(name = "daily_balance", nullable = false, precision = 9, scale = 2)
    BigDecimal dailyBalance;
    @Column(name = "daily_balance_reset_date", nullable = false, columnDefinition = "DATE")
    LocalDate dailyBalanceResetDate;
    @Column(name = "status", nullable = false)
    Boolean status;
}
