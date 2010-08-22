package ua.org.muromec.MediaSync;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import android.app.Activity;
import android.app.SearchManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ContextMenu;
import android.view.MenuItem;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import android.util.Log;
import android.content.Intent;

import org.json.JSONArray;

public class Browse extends Activity
{
    final String TAG = "Browse";
    protected List<HashMap<String, String>> elements;
    protected SimpleAdapter adapter;
    protected BrowseLoader loader = null;
    private List<String> keys = null;
    protected String nextFilterName = null;
    private int level = 0;
    protected ListView list;

    static private int CONTEXT_PLAYLIST = -1;

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

        setup();

    }

    protected void setup() {

        setContentView(getLayout());

        Intent intent = getIntent();

        level = intent.getIntExtra("level", 0 );
        List<String> req = intent.getStringArrayListExtra("req");
        loader = new BrowseLoader(req, MediaSync.server);

        setupList();
    }

    protected void setupList() {

        elements = new ArrayList<HashMap<String, String>>();

        adapter = new SimpleAdapter(this, elements,
            getListLayout(),
            getShowFields(),
            getShowLabels()
        );

        list = (ListView)findViewById( getListViewId());
        list.setAdapter(adapter);

        list.setOnItemClickListener(
          new OnItemClickListener () {
            public void onItemClick(AdapterView l, View v, int position, long id) {
                browse(position);
            }
          }
        );

        setupMenu();
        loader.load(JsonHandler);
    }

    protected void setupMenu() {
        if(level > 0) {
          list.setOnCreateContextMenuListener(contextMenu);
        }
    }

    private OnCreateContextMenuListener contextMenu = new OnCreateContextMenuListener() {

      public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
         menu.setHeaderTitle(getString(R.string.options));
          
         menu.add(0, CONTEXT_PLAYLIST, 0, R.string.playlist);

         for(int i=0; i<keys.size(); i++) {
           menu.add(1, i, 0, keys.get(i) );

         }
      }

    };

    @Override
    public boolean onContextItemSelected(MenuItem aItem) { 

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();

        if(aItem.getItemId() == CONTEXT_PLAYLIST) {
          nextFilterName = null;
        } else {
          nextFilterName = keys.get(aItem.getItemId());
        }

        browse(menuInfo.position);
        return true;
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

        
        if(jkeys.length() > 0 && level > 0) {
          nextFilterName = jkeys.getString(0);

          for(int i=0; i<jkeys.length(); i++) {
            keys.add(jkeys.getString(i));
          }

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

      ArrayList<String> nextReq = new ArrayList<String>(loader.req);

      if(level == 0) {
        nextFilterName = element;
      } else {
        nextReq.add(element);
      }

      if(nextFilterName != null) {
        nextReq.add(nextFilterName);
      }

      Class next = null;

      if(nextFilterName == null) {
        next = Playlist.class;
      } else {
        next = Browse.class;
      }

      Intent intent = new Intent(Browse.this, next);
      intent.putExtra("level", level +1 );
      intent.putExtra("req", nextReq);
      intent.putExtra("server", loader.server);

      startActivity(intent);
    }
}

