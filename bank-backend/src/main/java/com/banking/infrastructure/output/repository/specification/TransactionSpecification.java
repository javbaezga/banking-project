package com.banking.infrastructure.output.repository.specification;

import com.banking.infrastructure.output.repository.entity.TransactionEntity;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@UtilityClass
@SuppressWarnings("java:S1118")
public final class TransactionSpecification {
    @NonNull
    public static Specification<TransactionEntity> search(@Nullable final String transactionsSearchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (transactionsSearchTerm == null || transactionsSearchTerm.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            final var likeValue = "%" + transactionsSearchTerm.toLowerCase() + "%";
            final var accountPath = root.get("account");
            final var customerPath = accountPath.get("customer");
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(accountPath.get("number")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(customerPath.get("fullName")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(customerPath.get("idNumber")), likeValue),
                criteriaBuilder.like(
                    criteriaBuilder.lower(criteriaBuilder.function("TO_CHAR", String.class,
                        root.get("value"),
                        criteriaBuilder.literal("FM999999999.00")
                    )),
                    likeValue
                )
            );
        };
    }
}
