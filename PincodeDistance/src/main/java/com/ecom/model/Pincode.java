package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pincode {
	
	     @Id
	     @GeneratedValue(strategy = GenerationType.IDENTITY)
         private Long id;
	    private String pincode;
	    private double latitude;
	    private double longitude;
	    private String polygon;

}
