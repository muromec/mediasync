package ua.org.muromec.MediaSync;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.util.Log;

import android.os.Environment;

public class Dump implements Runnable {
    private HashMap<String, String> media;
    private String server;
    private String filename;

    public Dump(String s, HashMap<String, String> m) {
      media = m;
      server = s;
    }

    public String getURI() {

      StringBuilder uri = new StringBuilder();
      uri.append("http://");
      uri.append(server);
      uri.append("/fetch/");
      uri.append(media.get("fhash"));

      return uri.toString();

    }

    public void start() {

      Thread thread = new Thread(this);
      thread.start();

    }

    public void run() {
      dump();
    }

    public void dump() {

      FileOutputStream out = getOut();

      if(out == null) {
        return;
      }

      URL url;
      try {
        url = new URL(getURI());
      } catch (java.net.MalformedURLException e) {
        return;
      }

      HttpURLConnection httpc;
      DataInputStream in;

      try {
        httpc = (HttpURLConnection) url.openConnection();
        httpc.connect();

        in = new DataInputStream(httpc.getInputStream());
      } catch (java.io.IOException e) {
        return;
      }

      byte[] data = new byte[4096];
      int ret = 0;

      while(true) {
        try {
          ret = in.read(data, 0, data.length);

          if (ret <= 0) {
            break;
          }
          out.write(data, 0, ret);
        } catch ( java.io.EOFException e) {
          break;
        } catch ( java.io.IOException e) {
          return;
        }
        
      }

      try {
        out.close();
      } catch ( java.io.IOException e) {
        Log.d("DUMP", "cant close file o_O");
      }

    }

    public String getFilename() {
      return filename;
    }

    public FileOutputStream getOut() {
      File external = Environment.getExternalStorageDirectory();

      StringBuilder fname = new StringBuilder();
      fname.append( external.getPath() );
      fname.append( "/Music/" );

      if ( media.get("album") == null ) {
        fname.append("UnknownAlbum/");
      } else {
        fname.append(  media.get("album") );
        fname.append("/");
      }

      File dir = new File(fname.toString() );
      boolean ret = dir.mkdirs();

      if ( media.get("name") == null ) {
        fname.append("Unknownname");
      } else {
        fname.append(  media.get("name") );
      }

      fname.append( ".mp3" ); //FIXME

      filename = fname.toString();

      File out = new File(filename);

      try {
        out.createNewFile();

        return new FileOutputStream(out);
      } catch (java.io.IOException e) {
        Log.e("DUMP", "cant open out file: " + out.getPath() );
        e.printStackTrace();
        return null;
      }
    }
}

