package com.project.petcareapp.impl;

import com.project.petcareapp.model.Shop;
import com.project.petcareapp.repository.ShopRepository;
import com.project.petcareapp.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    ShopRepository shopRepository;


    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
}
