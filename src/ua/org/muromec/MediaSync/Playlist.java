package ua.org.muromec.MediaSync;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ContextMenu;
import android.view.MenuItem;

import android.widget.AdapterView.AdapterContextMenuInfo;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class Playlist extends Browse
{
    final String TAG = "PlayList";
    static private final int CONTEXT_DOWNLOAD = 0;
    static private final int CONTEXT_DOWNLOAD_ALL = 1;
    static private final int CONTEXT_PLAY = 2;

    @Override
    protected int getLayout() {
        return R.layout.playlist;
    }

    @Override
    protected int[] getShowLabels() {
        return new int[] {
            R.id.list_title,
            R.id.list_album,
        };
    }

    @Override
    protected int getListLayout() {
        return R.layout.list_media;
    }

    @Override
    protected String[] getShowFields() {
        return new String[]{
          "name",
          "album",
        };
    }

    @Override
    protected int getListViewId() {
        return R.id.media_list;
    }

    public void play(int position) {
        Dump dump = new Dump(loader.server, elements.get(position) );

        Uri uri = Uri.parse(dump.getURI());
        //dump.start();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }

    public void download(int position) {
        Intent dload = new Intent(Playlist.this, DLoadService.class);
        dload.putExtra("server", loader.server);
        dload.putExtra("media", elements.get(position) );
        startService(dload);
    }

    public void download() {
        for(int i=0; i<elements.size(); i++) {
            download(i);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem aItem) { 

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
        switch(aItem.getItemId()) {
          case CONTEXT_PLAY:
            play(menuInfo.position);
            break;
          case CONTEXT_DOWNLOAD:
            Log.d(TAG, "download id " + menuInfo.position);
            download(menuInfo.position);
            break;
          case CONTEXT_DOWNLOAD_ALL:
            Log.d(TAG, "download all");
            download();
            break;
        }

        return true;
    }

    private OnCreateContextMenuListener contextMenu = new OnCreateContextMenuListener() {

      public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
         menu.setHeaderTitle(getString(R.string.options));
          
         menu.add(0, CONTEXT_PLAY, 0, R.string.play);
         menu.add(0, CONTEXT_DOWNLOAD, 0, R.string.download);
         menu.add(0, CONTEXT_DOWNLOAD_ALL, 0, R.string.download_all);

      }

    };

    @Override
    protected void setupMenu() {
        list.setOnCreateContextMenuListener(contextMenu);
    }

    @Override
    protected void display(Object element_obj) {

      JSONObject json = (JSONObject) element_obj;

      HashMap<String, String> element = new HashMap<String, String>();

      Iterator jkeys = json.keys();

      while(jkeys.hasNext()) {
        String key = (String)jkeys.next();

        try{
          element.put(key, json.getString(key));
        } catch (org.json.JSONException e) {
          e.printStackTrace();
        }
      }

      elements.add(element);
      adapter.notifyDataSetChanged();
    }

}

