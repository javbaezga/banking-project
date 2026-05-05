package com.banking.infrastructure.output.repository.specification;

import com.banking.infrastructure.output.repository.entity.AccountEntity;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class AccountSpecification {
    @NonNull
    public static Specification<AccountEntity> search(@Nullable final String accountsSearchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (accountsSearchTerm == null || accountsSearchTerm.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            final var likeValue = "%" + accountsSearchTerm.toLowerCase() + "%";
            final var customerPath = root.get("customer");
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("number")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(customerPath.get("fullName")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(customerPath.get("idNumber")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), likeValue)
            );
        };
    }
}
