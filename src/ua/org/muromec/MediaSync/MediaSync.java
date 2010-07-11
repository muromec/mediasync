package ua.org.muromec.MediaSync;

import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.util.Log;

import ua.org.muromec.Util.JmDNSListener;

public class MediaSync extends ListActivity
{
    private JmDNSListener jmDNSListener = null;
    private MulticastLock fLock;
    private ArrayList<Bundle> discoveredServers = new ArrayList<Bundle>();
    private List<String> servers;
    private ArrayAdapter<String> adapter;

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

                servers.add(address);
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

      servers = new ArrayList<String>();

      adapter = new ArrayAdapter<String>(this, R.layout.list_item, servers);

      setListAdapter(adapter);

    }

}