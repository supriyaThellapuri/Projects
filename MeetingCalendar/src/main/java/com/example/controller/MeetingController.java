package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Employee;
import com.example.model.Meeting;
import com.example.model.TimeSlot;
import com.example.service.EmployeeService;
import com.example.service.MeetingService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
	
	@Autowired
	private MeetingService meetingService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping
	public ResponseEntity<String> bookMeeting(@RequestBody Meeting meeting){
		// Check if the meeting object has valid startTime and endTime
		if (meeting.getStartTime() == null || meeting.getEndTime() == null) {
			return ResponseEntity.badRequest().body("Start and end times must be provided");
		}
		
		meetingService.bookMeeting(meeting);
		return ResponseEntity.ok("Meeting booked successfully");
	}
	
	@GetMapping("/free-slots")
	public ResponseEntity<List<TimeSlot>> getFreeSlots(@RequestParam int employee1Id, @RequestParam int employee2Id, @RequestParam int duration){
		List<TimeSlot> freeSlots = meetingService.findFreeSlots(employee1Id, employee2Id, duration);
		return ResponseEntity.ok(freeSlots);
	}
	
	@PostMapping("/conflicts")
	public ResponseEntity<List<Employee>> findConflicts(@RequestBody Meeting meeting){
		List<Employee> conflicts = meetingService.findConflicts(meeting);
		return ResponseEntity.ok(conflicts);
	}
	
	@GetMapping
	public ResponseEntity<List<Meeting>> getAllMeetings() {
	    List<Meeting> meetings = meetingService.getAllMeetings();
	    return ResponseEntity.ok(meetings);
	}

}
