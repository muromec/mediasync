
package ua.org.muromec.MediaSync;

import android.os.Bundle;

import android.util.Log;
import android.content.Intent;

public class BrowseVariants extends Browse 
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        Log.d(TAG, "created variant for" + State.db);
    }

    public String url() {
        return "/db/"+State.db;
    }

    public void browse(int position) {
      String element = elements.get(position).get("name");
      State.query = element;

      Intent intent = new Intent(BrowseVariants.this, BrowseQuery.class);
      startActivity(intent);
    }

}
