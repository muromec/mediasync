package ua.org.muromec.MediaSync;

import java.util.HashMap;

import android.net.Uri;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.Context;

import android.util.Log;
import android.os.IBinder;

public class DLoadService extends IntentService {

  private static final String TAG = "DLOAD";
  private static int tasks = 0;

  public DLoadService() {
    super("DLoadServie");
  }
  
  protected void onHandleIntent (Intent intent) {

    HashMap media = (HashMap)intent.getSerializableExtra("media");
    String server = intent.getStringExtra("server");

    Log.d(TAG, "start load" + media.get("fhash"));


    Dump dump = new Dump(server, media);
    dump.dump();

    tasks -= 1;

    notify((String)media.get("name"));

    sendBroadcast(
        new Intent(
          Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
          Uri.parse("file://" + dump.getFilename() )
        )
    );


    Log.d(TAG, "ok, downloaded " + tasks);

  }

  public static void add(Context ctx, String server, HashMap media) {

    Intent dload = new Intent(ctx, DLoadService.class);
    dload.putExtra("server", server); // FIXME: remove
    dload.putExtra("media", media );
    ctx.startService(dload); // hmmm

    tasks += 1;

  }

  private void notify(String name) {
    String ns = Context.NOTIFICATION_SERVICE;
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

    CharSequence title = null;
    if (tasks > 0) {
      title = "Media downloaded " + tasks + " left";
    } else {
      title = "Media sync finished";
    }

    long when = System.currentTimeMillis();

    Notification notification = new Notification(R.drawable.stat_notify_musicplayer, title, when);
    notification.flags |= Notification.FLAG_AUTO_CANCEL;

        
    Intent notificationIntent = new Intent();
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    notification.setLatestEventInfo(this, title, name, contentIntent);

    mNotificationManager.notify(0, notification);

  }

}
