package com.application.specification;

import org.springframework.data.jpa.domain.Specification;

import com.application.model.FundRequest;
import com.application.model.User;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class FundRequestSpecification {
    public static Specification<FundRequest> filterFundRequests(
        Long id,
        Long userId, 
        String status, 
        Double minAmount, 
        Double maxAmount
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter
            if (userId != null) {
                Join<FundRequest, User> userJoin = root.join("user");
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
            }

            // Status filter
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Status filter
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            // Amount range filter
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}