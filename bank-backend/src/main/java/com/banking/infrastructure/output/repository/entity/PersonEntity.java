package com.banking.infrastructure.output.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "person",
    uniqueConstraints = {@UniqueConstraint(name = "ID_NUMBER_INDEX", columnNames = {"id_number"})},
    indexes = {@Index(name = "FULL_NAME_INDEX", columnList = "full_name")}
)
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonEntity {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(name = "full_name", nullable = false, length = 100)
    String fullName;
    @Column(name = "gender", nullable = false, length = 1)
    String gender;
    @Column(name = "age", nullable = false)
    Byte age;
    @Column(name = "id_number", nullable = false, length = 10)
    String idNumber;
    @Column(name = "address", nullable = false, length = 255)
    String address;
    @Column(name = "phone", nullable = false, length = 10)
    String phone;
}
