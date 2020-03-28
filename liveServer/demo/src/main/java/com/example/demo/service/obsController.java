package com.example.demo.service;


import com.example.demo.tool.myWebSocketClient;
import com.example.demo.tool.requsetType;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class  obsController {
    private myWebSocketClient client;
    private boolean streamingFlag=false;
    private String[] musicList;
    private int current=0;
    private Lock lock;



    private static String filePath="G:/CloudMusic/music/";

    public obsController() throws URISyntaxException, InterruptedException {
        File fileDir=new File(filePath);
        musicList=fileDir.list();
        client=new myWebSocketClient(new URI("ws://localhost:4444"));
        client.connectBlocking();
        client.setCallback(this::nextMusic);
        lock=new ReentrantLock(true);
        startStreaming();
    }


    public void startStreaming(){
        try {
            lock.lock();
            if(!streamingFlag) {
                JSONObject object = new JSONObject();
                object.put("request-type", requsetType.StartStreaming);
                object.put("message-id", requsetType.StartStreaming);
                client.send(object.toString());
                object.put("request-type",requsetType.SetSceneItemProperties);
                object.put("message-id", requsetType.SetSceneItemProperties);
                object.put("item","媒体源");
                object.put("visible",true);
                client.send(object.toString());
                streamingFlag=true;
                musicStart();
            }
        }finally {
            lock.unlock();
        }
    }

    public void stopStreaming(){
        try {
            lock.lock();
            if (streamingFlag) {
                JSONObject object = new JSONObject();
                object.put("request-type", requsetType.StopStreaming);
                object.put("message-id", requsetType.StopStreaming);
                streamingFlag = true;
            }
        }finally {
            lock.unlock();
        }
    }

    public void nextMusic(){
        try {
            lock.lock();
            current = current + 1;
            if (current == musicList.length) {
                current = 0;
            }
            musicStart();
        }finally {
            lock.unlock();
        }
    }

    public void prevMusic(){
        try {
            lock.lock();
            current = current - 1;
            if (current == -1) {
                current = musicList.length - 1;
            }
            musicStart();
        }finally {
            lock.unlock();
        }

    }

    private void musicStart(){
        JSONObject object=new JSONObject();
        object.put("request-type",requsetType.SetSourceSettings);
        object.put("message-id", requsetType.SetSourceSettings);
        object.put("sourceName","媒体源");
        Map<String,Object> settings=new HashMap<>();
        settings.put("local_file",filePath+this.musicList[current]);
        object.put("sourceSettings",settings);
        client.send(object.toString());
    }

}
