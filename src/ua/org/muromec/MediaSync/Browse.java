package ua.org.muromec.MediaSync;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.util.Log;
import android.content.Intent;

import org.json.JSONArray;

public class Browse extends Activity
{
    final String TAG = "Browse";
    protected List<HashMap<String, String>> elements;
    private SimpleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        Log.d(TAG, "created browse");
    }

    public String url() {
        return "/db";
    }

    @Override
    public void onResume() {
        super.onResume();

        elements = new ArrayList<HashMap<String, String>>();

        adapter = new SimpleAdapter(this, elements,
            R.layout.list_browse, 
            new String[] { "name" }, 
            new int[] { R.id.list_browse_name, } 
        );

        ListView list = (ListView)findViewById(R.id.element_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(
          new OnItemClickListener () {
            public void onItemClick(AdapterView l, View v, int position, long id) {
                browse(position);
            }
          }
        );
       
        /// send req
        Log.d(TAG, "resume");

        Log.d(TAG, "need to load database list from" + State.address);

        Request r = new Request(State.address, JsonHandler);
        r.setPath(url());

        Thread thread = new Thread(r);
      	thread.start();

    }

    protected void display(String element_str) {

      HashMap<String, String> element = new HashMap<String, String>();
      element.put("name", element_str);

      elements.add(element);
      adapter.notifyDataSetChanged();
    }

    public void parse(String data) {
      try {
        JSONArray parsed = new JSONArray(data);
        JSONArray o = parsed.getJSONArray(1);

        for (int i=0; i<o.length(); i++) {
          display(o.getString(i));
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    private Handler JsonHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        Bundle bundle = (Bundle) msg.getData();
        String data = bundle.getString("data");

        Log.d(TAG, "got data" + data);
        parse(data);

      }
    };

    public void browse(int position) {
      String element = elements.get(position).get("name");
      State.db = element;

      Intent intent = new Intent(Browse.this, BrowseVariants.class);
      startActivity(intent);
    }
}

