package ua.org.muromec.MediaSync;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.database.Cursor;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import android.widget.TextView;
import android.widget.SimpleAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnCreateContextMenuListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.ListView;

import android.util.Log;
import android.content.Intent;

import ua.org.muromec.Util.JmDNSListener;

public class MediaSync extends Activity
{
    private JmDNSListener jmDNSListener = null;
    private MulticastLock fLock;
    private List<HashMap<String, String>> servers = null;
    private SimpleAdapter adapter;
    private final String TAG = "MediaSync";
    private static final int CONTEXT_BROWSE = 0;
    private DBAdapter db;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        db = new DBAdapter(this);

        servers = new ArrayList<HashMap<String, String>>();
        loadOldServers();
        setupServerList();
        setupMDNS();
    }
    private Handler mDNSHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          Bundle bundle = (Bundle) msg.getData();
          Boolean add = bundle.getBoolean("add");
          String name = bundle.getString("name");

          if(add) {
            String address = bundle.getString("address");
            String uniq = bundle.getString("uniq");

            for(int i=0; i<servers.size();i++) {
              if (uniq.equals( servers.get(i).get("uniq") ) ) {
                servers.remove(i);
                break;
              }
            }

            HashMap<String, String> server = new HashMap<String, String>();
            server.put("name", name);
            server.put("address", address);
            server.put("uniq", uniq);
            server.put("white", bundle.getString("white"));

            servers.add(server);

          }  else {
            for(int i=0; i<servers.size();i++) {
              Log.d(TAG, servers.get(i).get("name") );

              if (name.equals( servers.get(i).get("name") ) ) {
                servers.remove(i);
                break;
              }
            }
          }

          adapter.notifyDataSetChanged();
        }
    };

    private byte[] intToIp(int i) {
		byte[] res = new byte[4];
		res[0] = (byte) (i & 0xff);
		res[1] = (byte) ((i >> 8) & 0xff);
		res[2] = (byte) ((i >> 16) & 0xff);
		res[3] = (byte) ((i >> 24) & 0xff);
		return res;
    }

    private void setupMDNS() {
        try {
          WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

          fLock = wifiManager.createMulticastLock("mediaservers");
	  fLock.acquire();

          byte[] wifiAddress = intToIp(wifiManager.getDhcpInfo().ipAddress);
          InetAddress wifi = InetAddress.getByAddress(wifiAddress);

          jmDNSListener = new JmDNSListener(mDNSHandler, wifi);
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }

    }

    private void loadOldServers() {
        db.open();
        Cursor cursor = db.getAllServers();
        if (cursor == null || cursor.getCount() == 0) {
          if (cursor != null) {
            cursor.close();
          }
          db.close();
          return;
        }


        int uniqIndex = cursor.getColumnIndexOrThrow("_uniq");
        int nameIndex = cursor.getColumnIndexOrThrow("server_name");
        int whiteIndex = cursor.getColumnIndexOrThrow("white");

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

          HashMap<String, String> server = new HashMap<String, String>();

          server.put("uniq", cursor.getString(uniqIndex));
          server.put("name", cursor.getString(nameIndex));
          server.put("address", cursor.getString(whiteIndex));
          servers.add(server);

          cursor.moveToNext();

        }
        cursor.close();
        db.close();
    }

    private void setupServerList() {
      adapter = new SimpleAdapter(this, servers, R.layout.list_item, new String[] { "name", "address" }, 
          new int[] { R.id.list_complex_name, R.id.list_complex_address } );


      ListView list = (ListView)findViewById(R.id.server_list);
      list.setAdapter(adapter);
      list.setOnItemClickListener(
          new OnItemClickListener () {
            public void onItemClick(AdapterView l, View v, int position, long id) {
                browse(position);
            }
          }
      );
        
      list.setOnCreateContextMenuListener(
          new OnCreateContextMenuListener() {
            public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(getString(R.string.options));
                menu.add(0, CONTEXT_BROWSE, 0, getString(R.string.browse_server));
            }

      });
    }

    @Override
    public boolean onContextItemSelected(MenuItem aItem) { 

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();

        browse(menuInfo.position);
        return true;
    }

    private void remember(HashMap<String, String> server) {
        if (! server.containsKey("white") ) {
            return;
        }

        db.open();
        db.deleteServer(server.get("uniq"));
        db.insertServer( 
            server.get("uniq"), server.get("name"), 
            server.get("address"), server.get("white")
        );
        db.close();
    }

    private void browse(int position) {

        HashMap<String, String> server = servers.get(position);
        remember(server);

        Intent intent = new Intent(MediaSync.this, Browse.class);
        intent.putExtra("level", 0);
        intent.putExtra("req", new ArrayList<String>());
        intent.putExtra("server", server.get( "address" ) );

        startActivity(intent);

    }

}
