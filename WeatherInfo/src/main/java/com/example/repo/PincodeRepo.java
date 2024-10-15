package com.example.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.PincodeInfo;

@Repository
public interface PincodeRepo extends JpaRepository<PincodeInfo, Long>{
	PincodeInfo findByPincode(String pincode);

}
