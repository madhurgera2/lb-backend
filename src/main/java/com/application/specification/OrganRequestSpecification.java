package com.application.specification;

import org.springframework.data.jpa.domain.Specification;

import com.application.model.OrganRequest;
import com.application.model.User;

import javax.persistence.criteria.*;

public class OrganRequestSpecification {
    public static Specification<OrganRequest> filterOrganRequests(
        Long userId,
        String organType,
        String status
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Filter by user ID if provided
            if (userId != null) {
                Join<OrganRequest, User> userJoin = root.join("user", JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(userJoin.get("id"), userId)
                );
            }

            // Filter by organ type if provided
            if (organType != null && !organType.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("organType"), organType)
                );
            }

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("status"), status)
                );
            }

            // Order by creation date descending
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return predicate;
        };
    }
}