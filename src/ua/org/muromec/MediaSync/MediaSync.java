package ua.org.muromec.MediaSync;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import android.widget.TextView;
import android.widget.SimpleAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnCreateContextMenuListener;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.ListView;

import android.util.Log;
import android.content.Intent;

import ua.org.muromec.Util.JmDNSListener;

public class MediaSync extends ListActivity
{
    private JmDNSListener jmDNSListener = null;
    private MulticastLock fLock;
    private List<HashMap<String, String>> servers;
    private SimpleAdapter adapter;
    private final String TAG = "MediaSync";
    private static final int CONTEXT_BROWSE = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    private Handler mDNSHandler = new Handler() {
        @Override
	public void handleMessage(Message msg) {
		Bundle bundle = (Bundle) msg.getData();
		String name = bundle.getString("name");
		String address = bundle.getString("address");

                HashMap<String, String> server = new HashMap<String, String>();
                server.put("name", name);
                server.put("address", address);

                servers.add(server);

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

    @Override
    public void onResume() {
        super.onResume();

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

      servers = new ArrayList<HashMap<String, String>>();

      adapter = new SimpleAdapter(this, servers, R.layout.list_item, new String[] { "name", "address" }, 
          new int[] { R.id.list_complex_name, R.id.list_complex_address } );

      setListAdapter(adapter);

      ListView list = getListView();
      list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(getString(R.string.options));
                menu.add(0, CONTEXT_BROWSE, 0, getString(R.string.browse_server));
            }

      });

    }

    @Override
    public boolean onContextItemSelected(MenuItem aItem) { 
        browse(aItem.getItemId());
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        browse(position);
    }

    private void browse(int position) {

        State.address = servers.get(position).get("address");
        Log.d(TAG, "connect to " + State.address);
        Intent intent = new Intent(MediaSync.this, Browse.class);
        startActivity(intent);

    }

}
