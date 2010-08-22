package ua.org.muromec.MediaSync;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;

import android.app.Activity;
import android.app.SearchManager;

import android.util.Log;

public class Search extends Playlist
{
    final String TAG = "Search";

    @Override
    protected void setup() {
        setContentView(getLayout());
        List<String> req = new ArrayList<String>();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          String query = intent.getStringExtra(SearchManager.QUERY);
          Log.d(TAG, "query " + query);

          req.add("search");
          req.add(query);

          loader = new BrowseLoader(req, MediaSync.server);

          setupList();
        }
    }
}

