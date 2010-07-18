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
    protected SimpleAdapter adapter;
    private BrowseLoader loader = null;
    private List<String> keys = null;
    protected String nextFilterName = null;

    // crap
    protected int getLayout() {
        return R.layout.browse;
    }

    protected int[] getShowLabels() {
        return new int[] {
          R.id.list_browse_name,
        };
    }

    protected int getListLayout() {
        return R.layout.list_browse;
    }

    protected String[] getShowFields() {
        return new String[]{
          "name"
        };
    }

    protected int getListViewId() {
        return R.id.element_list;
    }
    // end of crap

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 0 );
        String name = intent.getStringExtra("filtername");

        loader = new BrowseLoader(
            State.req, name, level
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        elements = new ArrayList<HashMap<String, String>>();

        adapter = new SimpleAdapter(this, elements,
            getListLayout(),
            getShowFields(),
            getShowLabels()
        );

        ListView list = (ListView)findViewById(
            getListViewId()
        );
        list.setAdapter(adapter);

        list.setOnItemClickListener(
          new OnItemClickListener () {
            public void onItemClick(AdapterView l, View v, int position, long id) {
                browse(position);
            }
          }
        );

        loader.load(JsonHandler);
    }

    protected void display(Object  element_obj) {



      HashMap<String, String> element = new HashMap<String, String>();
      element.put("name", (String)element_obj);

      elements.add(element);
      adapter.notifyDataSetChanged();
    }

    public void parse(String data) {

      try {
        JSONArray parsed = new JSONArray(data);
        JSONArray o = parsed.getJSONArray(1);
        JSONArray jkeys = parsed.getJSONArray(2);

        keys = new ArrayList<String>();

        for(int i=0; i<jkeys.length(); i++) {
          keys.add(jkeys.getString(i));
        }

        if(keys.size() > 0 && loader.level > 0) {
          nextFilterName = keys.get(0);
        } else {
          nextFilterName = null;
        }

        for (int i=0; i<o.length(); i++) {
          display((Object)o.get(i));
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

        parse(data);

      }
    };

    public void browse(int position) {
      String element = elements.get(position).get("name");

      if(loader.name == null) {
        nextFilterName = element;
      } else {

        while(State.req.size() + 1 > loader.level) {
          State.req.remove( State.req.size() - 1);
        }

        ArrayList filter = new ArrayList();
        filter.add(loader.name);
        filter.add(element);

        State.req.add(filter);

      }

      Class next = null;

      if(nextFilterName == null) {
        next = Playlist.class;
      } else {
        next = Browse.class;
      }

      Intent intent = new Intent(Browse.this, next);
      intent.putExtra("level", loader.level +1 );
      intent.putExtra("filtername", nextFilterName);

      startActivity(intent);
    }
}

