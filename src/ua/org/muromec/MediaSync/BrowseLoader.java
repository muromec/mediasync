package ua.org.muromec.MediaSync;

import java.util.List;
import java.net.URLEncoder;

import android.os.Handler;
import android.util.Log;

public class BrowseLoader {
  public static List<List<String>>req = null;

  public int level = 0;
  public String name = null;

  public BrowseLoader (List<List<String>>r, String n, int l) {
    level = l;
    req = r;
    name = n;
  }

  public void load(Handler h) {
    StringBuilder url = new StringBuilder();
    url.append("/db");

    for(int i=0;i<(level-1);i++) {
      List<String> filter = req.get(i);
      url.append("/");
      url.append(filter.get(0));
      url.append("/");
      try {
      url.append(URLEncoder.encode(filter.get(1), "UTF-8"));
      } catch (java.io.UnsupportedEncodingException e) {
      }
    }

    if(name != null) {
      url.append("/");
      url.append(name);
    }

    Request r = new Request(State.address, h);
    r.setPath(url.toString());

    Thread thread = new Thread(r);
    thread.start();

  }
  
}
