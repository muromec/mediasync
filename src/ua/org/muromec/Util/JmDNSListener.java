package ua.org.muromec.Util;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class JmDNSListener {
	private JmDNS jmdns = null;
	private Handler handler = null;

	private class Lookup extends Thread {
		private final ServiceEvent e;

		public Lookup(final ServiceEvent e) {
			this.e = e;
		}

		public void run() {
			ServiceInfo si = jmdns.getServiceInfo(e.getType(), e.getName());
			Log.v("JMDNSLISTENER", si.getHostAddress() + ":" + si.getPort());
			Bundle bundle = new Bundle();
			bundle.putString("name", si.getName());
			bundle.putString("address",
					si.getHostAddress() + ":" + si.getPort());
			Message msg = Message.obtain();
			msg.setTarget(handler);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	public JmDNSListener(Handler handler, InetAddress wifi) {
		try {
                        Log.d("MDNS", "start at " + wifi);
			this.handler = handler;
			jmdns = new JmDNS(wifi);
			jmdns.addServiceListener("_daap._tcp.local.",
					new ServiceListener() {
						@Override
						public void serviceResolved(ServiceEvent arg0) {
                                                    Log.d("MDNS", "resolved");
						}

						@Override
						public void serviceRemoved(ServiceEvent arg0) {
                                                    Log.d("MDNS", "removed");
						}

						@Override
						public void serviceAdded(ServiceEvent e) {
                                                        Log.d("MDNS", "added");
							Lookup l = new Lookup(e);
							l.start();
						}
					});
		} catch (IOException e) {
                        Log.d("MDNS", "AAAAA");
			e.printStackTrace();
		}
	}

	public void interrupt() {
		jmdns.close();
	}
}