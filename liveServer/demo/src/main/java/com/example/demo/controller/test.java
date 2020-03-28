package com.example.demo.controller;

import com.example.demo.service.obsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class test {

    @Autowired
    com.example.demo.service.obsController obsController;
    @RequestMapping("/")
    String home() {
        obsController.toString();
        return "Hello World!";
    }

}