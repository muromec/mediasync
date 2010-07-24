package ua.org.muromec.MediaSync;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class Playlist extends Browse
{
    final String TAG = "PlayList";

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

    @Override
    public void browse(int position) {
        Dump dump = new Dump(loader.server, elements.get(position) );

        Uri uri = Uri.parse(dump.getURI());
        //dump.start();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }

    @Override
    protected void setupMenu() { return; };

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

