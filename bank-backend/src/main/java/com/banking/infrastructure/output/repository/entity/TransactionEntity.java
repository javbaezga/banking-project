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
    name = "transaction",
    indexes = {@Index(name = "DATE_INDEX", columnList = "date")}
)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionEntity {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "FK_TRANSACTION_TO_ACCOUNT"))
    @ManyToOne(fetch = FetchType.EAGER)
    AccountEntity account;
    @Column(name = "date", nullable = false, columnDefinition = "DATE")
    LocalDate date;
    @Column(name = "type", nullable = false, length = 1)
    String type;
    @Column(name = "value", nullable = false, precision = 14, scale = 2)
    BigDecimal value;
    @Column(name = "balance", nullable = false, precision = 14, scale = 2)
    BigDecimal balance;
    @Column(name = "status", nullable = false)
    Boolean status;
    @Column(name = "description", nullable = false, length = 50)
    String description;
}
