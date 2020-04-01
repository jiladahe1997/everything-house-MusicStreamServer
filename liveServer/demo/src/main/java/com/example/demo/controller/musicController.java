package com.example.demo.controller;

import com.example.demo.service.obsController;
import com.example.demo.tool.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class musicController {

    @Autowired
    obsController obsController;

    @GetMapping("/music/start")
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

    @GetMapping("/music/prev")
    void musicPrev(){
        obsController.prevMusic();
    }

    @PostMapping("/music/orderSong")
    Response musicOrderSong(@RequestParam(value = "songName")String songName){
        return obsController.findMusic(songName);
    }

    @PostMapping("/music/finishDownLoad")
    void musicFinishDownLoad(@RequestParam(value = "songName")String songName){
        obsController.finishDownLoad(songName);
    }
}
