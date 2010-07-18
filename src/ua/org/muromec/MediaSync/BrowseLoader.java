package ua.org.muromec.MediaSync;

import java.util.List;
import java.net.URLEncoder;

import android.os.Handler;
import android.util.Log;

public class BrowseLoader {
  public List<String>req = null;
  public String server;

  public BrowseLoader (List<String>r, String a) {
    req = r;
    server = a;
  }

  public void load(Handler h) {
    StringBuilder url = new StringBuilder();

    url.append("/db");

    for(int i=0; i<req.size() ; i++) {
      url.append("/");
      try {
      url.append(URLEncoder.encode(req.get(i), "UTF-8"));
      } catch (java.io.UnsupportedEncodingException e) {
      }
    }


    Request r = new Request(server, h);
    r.setPath(url.toString());

    Thread thread = new Thread(r);
    thread.start();

  }
  
}
