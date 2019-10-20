package com.project.petcareapp.controller;

import com.project.petcareapp.model.Shop;
import com.project.petcareapp.repository.ShopRepository;
import com.project.petcareapp.service.ShopService;
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
public class ShopController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ShopService shopService;

    @GetMapping("/shops")
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }
}
