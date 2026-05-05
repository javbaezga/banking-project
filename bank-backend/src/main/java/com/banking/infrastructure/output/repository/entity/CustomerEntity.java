package com.banking.infrastructure.output.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "customer",
    uniqueConstraints = {@UniqueConstraint(name = "USERNAME_INDEX", columnNames = {"username"})}
)
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "FK_USER_TO_PERSON"))
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomerEntity extends PersonEntity {
    @Column(name = "username", nullable = false, length = 25)
    String username;
    @Column(name = "password", nullable = false, length = 50)
    String password;
    @Column(name = "status", nullable = false)
    Boolean status;
}
