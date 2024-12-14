package com.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.model.BloodBank;
import com.application.model.BloodDonation;
import java.util.List;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
    List<BloodBank> findByBloodDonation(BloodDonation bloodDonation);

    @Query("SELECT COALESCE(SUM(bb.approvedUnits), 0) FROM BloodBank bb WHERE bb.bloodGroup = :bloodGroup AND bb.status = 'USABLE'")
    Double sumUsableUnitsByBloodGroup(@Param("bloodGroup") String bloodGroup);

    @Query("SELECT bb.bloodGroup, COALESCE(SUM(bb.approvedUnits), 0) FROM BloodBank bb WHERE bb.status = 'USABLE' GROUP BY bb.bloodGroup")
    List<Object[]> getBloodGroupInventory();

    @Query("SELECT bb FROM BloodBank bb WHERE bb.bloodGroup = :bloodGroup AND bb.status = 'USABLE' ORDER BY bb.createdAt ASC")
    List<BloodBank> findOldestUsableBloodByGroup(
        @Param("bloodGroup") String bloodGroup
    );
}