package com.project.petcareapp.impl;

import com.project.petcareapp.model.Services;
import com.project.petcareapp.repository.ServiceRepository;
import com.project.petcareapp.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicesServiceImpl implements ServicesService {

    @Autowired
    ServiceRepository serviceRepository;

    @Override
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }
}
