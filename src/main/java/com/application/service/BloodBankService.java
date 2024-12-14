package com.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.repository.BloodBankRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BloodBankService {
    @Autowired
    private BloodBankRepository bloodBankRepository;

    @Transactional(readOnly = true)
    public Double getTotalUsableUnitsByBloodGroup(String bloodGroup) {
        // Validate blood group
        validateBloodGroup(bloodGroup);

        // Query to get total usable units for a specific blood group
        Double totalUnits = bloodBankRepository.sumUsableUnitsByBloodGroup(bloodGroup);
        
        return totalUnits != null ? totalUnits : 0.0;
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getBloodGroupInventory() {
        // Get total usable units for each blood group
        List<Object[]> inventoryResults = bloodBankRepository.getBloodGroupInventory();
        
        Map<String, Double> inventory = new HashMap<>();
        for (Object[] result : inventoryResults) {
            String bloodGroup = (String) result[0];
            Double totalUnits = ((Number) result[1]).doubleValue();
            inventory.put(bloodGroup, totalUnits);
        }
        
        return inventory;
    }

    private void validateBloodGroup(String bloodGroup) {
        // List of valid blood groups
        String[] validBloodGroups = {
            "A+", "A-", 
            "B+", "B-", 
            "AB+", "AB-", 
            "O+", "O-"
        };

        // Check if the provided blood group is valid
        for (String validGroup : validBloodGroups) {
            if (validGroup.equalsIgnoreCase(bloodGroup)) {
                return;
            }
        }

        throw new IllegalArgumentException("Invalid blood group: " + bloodGroup);
    }
}