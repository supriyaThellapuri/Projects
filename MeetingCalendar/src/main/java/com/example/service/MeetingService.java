package com.example.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Employee;
import com.example.model.Meeting;
import com.example.model.TimeSlot;
import com.example.repository.EmployeeRepository;
import com.example.repository.MeetingRepository;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Booking a meeting and saving it to the repository
    public Meeting bookMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public List<TimeSlot> findFreeSlots(int employee1Id, int employee2Id, int duration) {
        // Retrieve meetings for both employees
        List<Meeting> meetings = meetingRepository.findMeetingsByEmployees(List.of(employee1Id, employee2Id));

        // Sort meetings by start time
        meetings.sort(Comparator.comparing(Meeting::getStartTime));

        List<TimeSlot> freeSlots = new ArrayList<>();
        
        // Define the working hours (customize these based on your requirements)
        LocalDateTime startOfDay = LocalDateTime.of(2024, 10, 15, 8, 0); // Example start time
        LocalDateTime endOfDay = LocalDateTime.of(2024, 10, 15, 17, 0); // Example end time

        // If there are no meetings, the entire day is free
        if (meetings.isEmpty()) {
            freeSlots.add(new TimeSlot(startOfDay, endOfDay));
            return freeSlots;
        }

        // Check for free slots before the first meeting
        if (startOfDay.isBefore(meetings.get(0).getStartTime())) {
            freeSlots.add(new TimeSlot(startOfDay, meetings.get(0).getStartTime()));
        }

        // Check for gaps between meetings
        for (int i = 0; i < meetings.size() - 1; i++) {
            Meeting currentMeeting = meetings.get(i);
            Meeting nextMeeting = meetings.get(i + 1);
            LocalDateTime endOfCurrentMeeting = currentMeeting.getEndTime();
            
            // Check for gap between current and next meeting
            if (endOfCurrentMeeting.isBefore(nextMeeting.getStartTime())) {
                LocalDateTime gapStart = endOfCurrentMeeting;
                LocalDateTime gapEnd = nextMeeting.getStartTime();

                // Check if the gap is enough for the specified duration
                if (gapEnd.minusMinutes(duration).isAfter(gapStart)) {
                    freeSlots.add(new TimeSlot(gapStart, gapEnd));
                }
            }
        }

        // Check for free slots after the last meeting
        if (meetings.get(meetings.size() - 1).getEndTime().isBefore(endOfDay)) {
            freeSlots.add(new TimeSlot(meetings.get(meetings.size() - 1).getEndTime(), endOfDay));
        }

        return freeSlots;
    }

    // Find conflicting employees for a given meeting
    public List<Employee> findConflicts(Meeting meeting) {
        // Get all meetings for the employee who is trying to book the meeting
        List<Meeting> existingMeetings = meetingRepository.findByEmployeeId(meeting.getEmployee().getId());

        List<Employee> conflictingParticipants = new ArrayList<>();

        // Check if the meeting time overlaps with any existing meetings
        for (Meeting existingMeeting : existingMeetings) {
            if (meeting.getStartTime().isBefore(existingMeeting.getEndTime()) &&
                meeting.getEndTime().isAfter(existingMeeting.getStartTime())) {
                conflictingParticipants.add(existingMeeting.getEmployee());
            }
        }

        return conflictingParticipants;
    }

    // Find an employee by their ID
    public Employee findEmployeeById(int employeeId) {
        return employeeRepository.findById(employeeId).orElse(null); // Adjusted for integer ID
    }

	public List<Meeting> getAllMeetings() {
		// TODO Auto-generated method stub
		return meetingRepository.findAll();
	}
}
