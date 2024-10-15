package com.example;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.controller.MeetingController;
import com.example.model.Meeting;
import com.example.model.TimeSlot; // Make sure you import TimeSlot if used
import com.example.service.MeetingService;
import com.fasterxml.jackson.databind.ObjectMapper;

class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private MeetingController meetingController; // Assuming you have a MeetingController

    @Mock
    private MeetingService meetingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(meetingController).build();
    }

    @Test
    void testBookMeeting() throws Exception {
        // Arrange
        Meeting meeting = new Meeting();
        meeting.setId(1); // Assuming ID is of type int
        meeting.setTitle("Test Meeting");
        meeting.setStartTime(LocalDateTime.now().plusMinutes(30));
        meeting.setEndTime(LocalDateTime.now().plusMinutes(60));

        when(meetingService.bookMeeting(any(Meeting.class))).thenReturn(meeting);

        // Act & Assert
        mockMvc.perform(post("/meetings") // Adjust the endpoint to match your actual endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(meeting)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Meeting"));
    }

    @Test
    void testFindFreeSlots() throws Exception {
        // Arrange
        int employee1Id = 1;
        int employee2Id = 2;
        int duration = 30;

        // Assuming the service returns a list of time slots
        when(meetingService.findFreeSlots(employee1Id, employee2Id, duration)).thenReturn(new ArrayList<>()); // Use a proper list of TimeSlot if needed

        // Act & Assert
        mockMvc.perform(get("/meetings/free-slots") // Adjust the endpoint to match your actual endpoint
                .param("employee1Id", String.valueOf(employee1Id)) // Convert to String for URL param
                .param("employee2Id", String.valueOf(employee2Id)) // Convert to String for URL param
                .param("duration", String.valueOf(duration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()); // Expecting an array response
    }

    @Test
    void testFindConflicts() throws Exception {
        // Arrange
        Meeting meeting = new Meeting();
        meeting.setId(1); // Assuming ID is of type int
        meeting.setStartTime(LocalDateTime.now().plusMinutes(10));
        meeting.setEndTime(LocalDateTime.now().plusMinutes(40));

        when(meetingService.findConflicts(any(Meeting.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/meetings/conflicts") // Adjust the endpoint to match your actual endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(meeting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()); // Expecting an array response
    }
}
