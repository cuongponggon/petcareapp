package com.project.petcareapp.controller;


import com.project.petcareapp.model.Services;
import com.project.petcareapp.repository.ServiceRepository;
import com.project.petcareapp.service.ServicesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ServicesController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesController.class);

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServicesService servicesService;

    @GetMapping("/services")
    public List<Services> getAllServices() {
        return servicesService.getAllServices();
    }
}
