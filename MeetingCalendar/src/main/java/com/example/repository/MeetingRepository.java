package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, String>{
//
//	List<Meeting> findByEmployeeId(int i);
	@Query("SELECT m FROM Meeting m WHERE m.employee.id IN :employeeIds")
    List<Meeting> findMeetingsByEmployees(@Param("employeeIds") List<Integer> employeeIds);

	List<Meeting> findByEmployeeId(int id);
	
}
