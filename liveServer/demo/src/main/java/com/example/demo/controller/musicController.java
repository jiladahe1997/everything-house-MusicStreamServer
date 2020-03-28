package com.example.demo.controller;

import com.example.demo.service.obsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class musicController {

    @Autowired
    obsController obsController;

    @GetMapping("/muisc/start")
    void musicStart(){
        obsController.startStreaming();
    }

    @GetMapping("/music/stop")
    void musicStop(){
        obsController.stopStreaming();
    }

    @GetMapping("/music/next")
    void musicNext(){
        obsController.nextMusic();
    }

    @GetMapping("/music/next")
    void musicPrev(){
        obsController.prevMusic();
    }
}
