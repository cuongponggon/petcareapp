package com.project.petcareapp.controller;


import com.project.petcareapp.model.MyMessage;
import com.project.petcareapp.repository.MyMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
//@RequestMapping(AccountController.BASE_URK)
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
public class MyMessageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMessageController.class);


    @Autowired
    MyMessageRepository myMessageRepository;






    @RequestMapping(value = "/messages",produces = "application/json",method = RequestMethod.POST)
    public MyMessage createMessage(@Valid @RequestBody MyMessage myMessage) {
        return myMessageRepository.save(myMessage);
    }










}
