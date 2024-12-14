package com.application.specification;

import org.springframework.data.jpa.domain.Specification;

import com.application.model.BloodDonation;
import com.application.model.User;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BloodDonationSpecification {
    public static Specification<BloodDonation> filterBloodDonations(
        Long userId, 
        String status, 
        String bloodGroup
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter
            if (userId != null) {
                Join<BloodDonation, User> userJoin = root.join("user");
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
            }

            // Status filter
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}