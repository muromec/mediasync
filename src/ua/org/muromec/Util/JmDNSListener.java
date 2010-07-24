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
        private final String TAG = "JMDNSLISTENER";
        private static int ADD = 1;
        private static int REM = 0;

	private class Lookup extends Thread {
		private final ServiceEvent e;
                private int mode;

		public Lookup(final ServiceEvent e){
			this.e = e;
		}

		public void run() {
			ServiceInfo si = jmdns.getServiceInfo(e.getType(), e.getName());

                        if ( si.getTextString() == null) {
                          return;
                        }

                        String[] text = si.getTextString().split("\t");
                        String address = si.getHostAddress() + ":" + si.getPort();
                        Log.d(TAG, si.getTextString());

			Bundle bundle = new Bundle();
			bundle.putString("name", e.getName());
			bundle.putString("address", address);
                        bundle.putString("uniq", text[0]);
                        bundle.putString("white", text[1]);
                        bundle.putBoolean("add", true);

			Message msg = Message.obtain();
			msg.setTarget(handler);
			msg.setData(bundle);

			handler.sendMessage(msg);
		}
	}

        private ServiceListener mdnsListener = new ServiceListener() {
          @Override
          public void serviceResolved(ServiceEvent arg0) {
          }

          @Override
          public void serviceRemoved(ServiceEvent e) {
            Bundle bundle = new Bundle();
	    bundle.putString("name", e.getName());
            bundle.putBoolean("add", false);

            Message msg = Message.obtain();
	    msg.setTarget(handler);
	    msg.setData(bundle);
			
            handler.sendMessage(msg);

          }

          @Override
          public void serviceAdded(ServiceEvent e) {
            Lookup l = new Lookup(e);
            l.start();
          }
        };

        public JmDNSListener(Handler handler, InetAddress wifi) {
          try {
            Log.d(TAG, "start at " + wifi);
            this.handler = handler;
            jmdns = new JmDNS(wifi);
            jmdns.addServiceListener("_jpop._tcp.local.", mdnsListener);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

	public void interrupt() {
		jmdns.close();
	}
}
