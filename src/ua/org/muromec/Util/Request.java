package ua.org.muromec.MediaSync;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.DataInputStream;
import java.lang.Thread;

import org.json.JSONArray;

import android.util.Log;

public class Request implements Runnable {
    String base = null; 
    String savedPath = null;
    int len = 0;

    JSONArray decoded = null;

    protected HttpURLConnection httpc;
    public byte[] data;

    public Request(String address) {
        base = "http://" + address;
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
            
            int response_code = httpc.getResponseCode();

            Log.d("Req", "got code" + response_code);

            String msg = httpc.getResponseMessage();

            Log.d("Req", "got msg" + msg);

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
        decoded = new JSONArray(
            new String(data, 0, len, "UTF-8" )
        );

        Log.d("Req", "array" + decoded.getJSONArray(1) );
      } catch (Exception e) {
        e.printStackTrace();
      }

    }


}
