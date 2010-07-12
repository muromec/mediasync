package ua.org.muromec.MediaSync;

import android.app.ListActivity;
import android.os.Bundle;

import android.util.Log;

public class Browse extends ListActivity
{
    final String TAG = "Browse";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "created");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resume");

        Log.d(TAG, "need to load database list from" + State.address);
    }

}

