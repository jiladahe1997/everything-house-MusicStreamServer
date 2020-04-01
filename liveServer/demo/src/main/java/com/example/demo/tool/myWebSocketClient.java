package com.example.demo.tool;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class myWebSocketClient extends WebSocketClient {

    private callBack callback;
    public myWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setCallback(callBack callback){
        this.callback=callback;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        JSONObject response=new JSONObject(s);
        if(response.has("status")&&response.getString("status").equals("error")){
            System.out.println(response.getString("message-id"));
            System.out.println(response.getString("error"));
        }
        else if(response.has("update-type")&&response.getString("update-type").equals("SceneItemTransformChanged")){
            System.out.println(s);
            JSONObject transform=response.getJSONObject("transform");
            if(transform.getInt("sourceHeight")==0&&transform.getInt("sourceWidth")==0){
               callback.nextMusicCallback();
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public interface callBack{
        public void nextMusicCallback();
    }

    @Override
    public void send(String text) {
        if(this.isClosed()){
            try {
                this.reconnectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.send(text);
    }
}
