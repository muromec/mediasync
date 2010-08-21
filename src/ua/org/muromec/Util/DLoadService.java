package ua.org.muromec.MediaSync;

import java.util.HashMap;
import android.net.Uri;

import android.app.IntentService;
import android.content.Intent;

import android.util.Log;
import android.os.IBinder;

public class DLoadService extends IntentService {

  private static final String TAG = "DLOAD";

  public DLoadService() {
    super("DLoadServie");
  }
  
  protected void onHandleIntent (Intent intent) {

    HashMap media = (HashMap)intent.getSerializableExtra("media");
    String server = intent.getStringExtra("server");

    Log.d(TAG, "start load" + media.get("fhash"));

    Dump dump = new Dump(server, media);
    dump.dump();

    sendBroadcast(
        new Intent(
          Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
          Uri.parse("file://" + dump.getFilename() )
        )
    );


    Log.d(TAG, "ok, downloaded");
  }

  public void onDestroy () {
    Log.d(TAG, "all downloaded?");
    super.onDestroy();
  }

}
