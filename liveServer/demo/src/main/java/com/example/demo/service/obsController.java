package com.example.demo.service;


import com.example.demo.tool.Response;
import com.example.demo.tool.myWebSocketClient;
import com.example.demo.tool.requsetType;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class  obsController {
    private myWebSocketClient client;
    private boolean streamingFlag=false;
    private List<String> musicList;
    private Queue<String> downLoadList;
    private List<String> downLoadingList;
    private int current=0;
    private Lock lock;



//    private static String filePath="G:/CloudMusic/music/";
    private static String filePath="C:/music/";

    public obsController() throws URISyntaxException, InterruptedException {
        musicList=new ArrayList<>();
        downLoadList=new LinkedList<>();
        downLoadingList=new ArrayList<>();
        client=new myWebSocketClient(new URI("ws://localhost:4444"));
        client.connectBlocking();
        client.setCallback(this::nextMusic);
        lock=new ReentrantLock(true);
        JSONObject object =new JSONObject();
        object.put("request-type",requsetType.SetHeartbeat);
        object.put("message-id",requsetType.SetHeartbeat);
        object.put("enable",true);
        client.send(object.toString());
    }


    public void startStreaming(){
        File fileDir=new File(filePath);
        musicList.addAll(Arrays.asList(Objects.requireNonNull(fileDir.list())));
        JSONObject object = new JSONObject();
        object.put("request-type", requsetType.StartStreaming);
        object.put("message-id", requsetType.StartStreaming);
        JSONObject object1 = new JSONObject();
        object1.put("request-type",requsetType.SetSceneItemProperties);
        object1.put("message-id", requsetType.SetSceneItemProperties);
        object1.put("item","媒体源");
        object1.put("visible",true);
        try {
            lock.lock();
            if(!streamingFlag) {
                client.send(object.toString());
                client.send(object1.toString());
                streamingFlag=true;
            }
            if(musicList.size()!=0){
                musicStart();
            }
        }finally {
            lock.unlock();
        }
    }

    public void stopStreaming(){
        JSONObject object = new JSONObject();
        object.put("request-type", requsetType.StopStreaming);
        object.put("message-id", requsetType.StopStreaming);
        JSONObject object1 = new JSONObject();
        object1.put("request-type",requsetType.SetSceneItemProperties);
        object1.put("message-id", requsetType.SetSceneItemProperties);
        object1.put("item","媒体源");
        object1.put("visible",false);
        try {
            lock.lock();
            if (streamingFlag) {
                client.send(object.toString());
                client.send(object1.toString());
                streamingFlag = false;
            }
        }finally {
            lock.unlock();
        }
    }

    public void nextMusic(){
        try {
            lock.lock();
            current = current + 1;
            if (current == musicList.size()) {
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
                current = musicList.size() - 1;
            }
            musicStart();
        }finally {
            lock.unlock();
        }

    }

    public Response findMusic(String music){
        if(musicList.contains(music)||downLoadList.contains(music)){
            return new Response(1,"found");
        }
        else if(downLoadingList.contains(music)){
            return new Response(1,"downloading");
        }
        else{
            return new Response(1,"not found");
        }
    }

    public void finishDownLoad(String music){
        lock.lock();
        if(!downLoadList.contains(music)){
            downLoadList.add(music);
        }
        lock.unlock();
    }

    private void musicStart(){
        JSONObject object=new JSONObject();
        object.put("request-type",requsetType.SetSourceSettings);
        object.put("message-id", requsetType.SetSourceSettings);
        object.put("sourceName","媒体源");
        Map<String,Object> settings=new HashMap<>();
        if(downLoadList.size()!=0) {
            String musicName=downLoadList.poll();
            if(!musicList.contains(musicName)) {
                musicList.add(musicName);
            }
            settings.put("local_file", filePath +musicName);
        }
        else {
            settings.put("local_file", filePath + musicList.get(current));
        }
        object.put("sourceSettings",settings);
        client.send(object.toString());
    }


}
