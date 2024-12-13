package com.application.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fund_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fund_request_id", nullable = false)
    @JsonBackReference("fund-request-donation")
    private FundRequest fundRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-fund-donation")
    private User user;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Minimum donation is 1")
    private Double amount;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "SUCCESS";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}