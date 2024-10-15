package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Employee;
import com.example.repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	
	public Employee findEmployeeId(int employeeId) {
		return employeeRepository.findById(employeeId).orElseThrow(()-> new RuntimeException("Employee not found with ID:" + employeeId));
	}
	

}
