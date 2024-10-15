package com.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.model.DistanceResponse;
import com.ecom.model.PincodeRequest;
import com.ecom.service.DistanceService;

@RestController
@RequestMapping("/api")
public class DistanceController {

    private final DistanceService distanceService;

    @Autowired
    public DistanceController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }
    
    @PostMapping("/distance")
    public DistanceResponse calculateDistance(@RequestBody PincodeRequest request) {
        return distanceService.calculateDistance(request.getFromPincode(), request.getToPincode());
    }

    @GetMapping("/distance")
    public DistanceResponse getDistance(@RequestParam String fromPincode, @RequestParam String toPincode) {
        return distanceService.calculateDistance(fromPincode, toPincode);
    }
}
