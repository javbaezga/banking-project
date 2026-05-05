package com.banking.infrastructure.output.repository.specification;

import com.banking.infrastructure.output.repository.entity.CustomerEntity;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class CustomerSpecification {
    @NonNull
    public static Specification<CustomerEntity> search(@Nullable final String customersSearchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (customersSearchTerm == null || customersSearchTerm.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            final var likeValue = "%" + customersSearchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("idNumber")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likeValue)
            );
        };
    }
}
