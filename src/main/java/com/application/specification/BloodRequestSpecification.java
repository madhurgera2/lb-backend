package com.application.specification;

import org.springframework.data.jpa.domain.Specification;

import com.application.model.BloodRequest;
import com.application.model.User;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestSpecification {
    public static Specification<BloodRequest> filterBloodRequests(
        Long userId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter
            if (userId != null) {
                Join<BloodRequest, User> userJoin = root.join("user");
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}