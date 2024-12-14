package com.application.model;

import javax.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organ_requests")
public class OrganRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organ_type", nullable = false)
    private String organType;

    @Column(name = "units", nullable = false)
    private Double units;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "require_before")
    private LocalDateTime requireBefore;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_units")
    private Double approvedUnits;

    @ManyToOne
    @JoinColumn(name = "processed_by_id")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}