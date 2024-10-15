package com.example.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generate the ID
    private int id;
    
    private String title;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false) // Foreign key column name
    private Employee employee; // The employee who created this meeting

    @ManyToMany // Change to ManyToMany
    @JoinTable( // Specify the join table
        name = "meeting_participants", // The name of the join table
        joinColumns = @JoinColumn(name = "meeting_id"), // Foreign key from Meeting
        inverseJoinColumns = @JoinColumn(name = "employee_id") // Foreign key from Employee
    )
    private List<Employee> participants; // List of participants in the meeting

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Additional constructor for tests
    public Meeting(int id, String title, LocalDateTime startTime, LocalDateTime endTime, Employee employee) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employee = employee;
        this.participants = List.of(); // Initialize participants as an empty list
    }
}
