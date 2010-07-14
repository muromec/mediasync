
package ua.org.muromec.MediaSync;

import java.net.URLEncoder;
import java.util.Iterator;


import org.json.JSONObject;
import org.json.JSONArray;

import android.os.Bundle;

import android.util.Log;

public class BrowseQuery extends Browse 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        Log.d(TAG, "created query for" + State.query);
    }

    public String url() {
        return "/db/"+State.db+"/"+URLEncoder.encode(
            State.query
        );
    }

    public void parse(String data) {
        try {
          JSONArray parsed = new JSONArray(data);
          JSONObject o = parsed.getJSONObject(1);

          Iterator<String> it = o.keys();

          while(it.hasNext()) {
            String key = it.next();

            JSONObject item = o.getJSONObject(key);
            display(item.getString("name"));
          }


        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    public void browse(int position) {
      return;
    }

}
