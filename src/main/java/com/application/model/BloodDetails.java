package com.application.model;

import java.time.Instant;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

public class BloodDetails 
{
	private String bloodgroup;
	private int count;
	private int units;
	
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;
	
	public BloodDetails(String bloodgroup, int count, int units)
	{
		this.bloodgroup = bloodgroup;
		this.count = count;
		this.units = units;
	}

	public String getBloodgroup() {
		return bloodgroup;
	}

	public void setBloodgroup(String bloodgroup) {
		this.bloodgroup = bloodgroup;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}
}