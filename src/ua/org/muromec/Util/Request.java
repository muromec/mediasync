package ua.org.muromec.MediaSync;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.DataInputStream;
import java.lang.Thread;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

import android.util.Log;

public class Request implements Runnable {
    String base = null; 
    String savedPath = null;
    Handler handler = null;
    int len = 0;

    protected HttpURLConnection httpc;
    public byte[] data;

    public Request(String address, Handler h) {
        base = "http://" + address;
        handler = h;
    }

    public void setPath(String path) {
        savedPath = path;
    }

    public void run() {
        load(savedPath);
    }

    public void load(String path) {
        try {
            URL url = new URL(base + path);
            Log.d("Req", "loading " + base + path);

            httpc = (HttpURLConnection) url.openConnection();
            httpc.connect();
            
            DataInputStream in = new DataInputStream(httpc.getInputStream());
      
            len = httpc.getContentLength();
      
            if (len <= 0) {
              return;
            }
      
            data = new byte[len];
      
            in.readFully(data);

            parse();


        } catch (IOException e) {
            Log.d("Req", "ou...");
            e.printStackTrace();
        }

    }

    private void parse() {
        
      try {
        Message msg = Message.obtain();
        Bundle bd = new Bundle();

        bd.putString("data", new String(data, 0, len, "UTF-8" ) );
        msg.setData(bd);

        handler.sendMessage(msg);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }


}
