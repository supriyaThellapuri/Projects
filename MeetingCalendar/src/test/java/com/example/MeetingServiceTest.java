package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.model.Employee;
import com.example.model.Meeting;
import com.example.model.TimeSlot;
import com.example.repository.EmployeeRepository;
import com.example.repository.MeetingRepository;
import com.example.service.MeetingService;

class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService; // Testing MeetingService

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookMeeting() {
        // Arrange
        Meeting meeting = new Meeting();
        meeting.setId(1);
        meeting.setTitle("Test Meeting");
        meeting.setStartTime(LocalDateTime.now().plusMinutes(30));
        meeting.setEndTime(LocalDateTime.now().plusMinutes(60));

        // Mock the repository's save method to return the meeting object
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);

        // Act
        Meeting bookedMeeting = meetingService.bookMeeting(meeting);

        // Assert
        assertNotNull(bookedMeeting);
        assertEquals("Test Meeting", bookedMeeting.getTitle());
    }

    @Test
    void testFindFreeSlots() {
        // Arrange
        LocalDateTime expectedStartTime = LocalDateTime.now().plusMinutes(30);
        Employee employee1 = new Employee();
        employee1.setId(1); // Set employee ID as int
        Employee employee2 = new Employee();
        employee2.setId(2); // Set employee ID as int

        // Mock meetings for both employees
        List<Meeting> meetings1 = new ArrayList<>();
        meetings1.add(new Meeting(1, "Meeting 1", expectedStartTime.minusMinutes(15), expectedStartTime.plusMinutes(15), employee1));

        List<Meeting> meetings2 = new ArrayList<>();
        meetings2.add(new Meeting(2, "Meeting 2", expectedStartTime.plusMinutes(30), expectedStartTime.plusMinutes(60), employee2));

        when(meetingRepository.findByEmployeeId(employee1.getId())).thenReturn(meetings1);
        when(meetingRepository.findByEmployeeId(employee2.getId())).thenReturn(meetings2);

        // Act
        List<TimeSlot> actualFreeSlots = meetingService.findFreeSlots(employee1.getId(), employee2.getId(), 30);

        // Assert
        assertEquals(1, actualFreeSlots.size());
        assertEquals(expectedStartTime, actualFreeSlots.get(0).getStartTime());
    }

    @Test
    void testFindConflicts() {
        // Arrange
        Meeting meeting = new Meeting();
        meeting.setId(1);
        meeting.setStartTime(LocalDateTime.now().plusMinutes(10));
        meeting.setEndTime(LocalDateTime.now().plusMinutes(40));

        Employee employee1 = new Employee();
        employee1.setId(1); // Set employee ID as int

        List<Meeting> existingMeetings = new ArrayList<>();
        existingMeetings.add(new Meeting(2, "Existing Meeting", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(30), employee1));

        when(meetingRepository.findByEmployeeId(employee1.getId())).thenReturn(existingMeetings);

        // Act
        List<Employee> actualConflicts = meetingService.findConflicts(meeting);

        // Assert
        assertEquals(1, actualConflicts.size());
        assertEquals(1, actualConflicts.get(0).getId()); // Ensure to compare as int
    }

    @Test
    void testFindEmployeeById() {
        // Arrange
        Employee employee = new Employee();
        employee.setId(1); // Set employee ID as int
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee)); // Pass int

        // Act
        Employee foundEmployee = meetingService.findEmployeeById(1); // Pass int

        // Assert
        assertNotNull(foundEmployee);
        assertEquals(1, foundEmployee.getId()); // Ensure to compare as int
    }
}
