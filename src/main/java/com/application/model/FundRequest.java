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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fund_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-fund-request")
    private User user;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Minimum request amount is 1")
    private Double amount;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-fund-request")
    private User doctor;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}