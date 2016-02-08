package fistbumpstudios.wifidirecttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity activity;
    public static WifiP2pManager.PeerListListener myPeerListListener;
    private Set<String> connected_devices = new HashSet<String>();
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity)
    {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.activity = activity;
    }

    public void clearConnection()
    {
        connected_devices.clear();
        peers.clear();
    }

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());


        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener( ) {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

            // InetAddress from WifiP2pInfo struct.
            InetAddress groupOwnerAddress = info.groupOwnerAddress;

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
                activity.make_server_thread();
            } else if (info.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
                activity.make_client_thread(groupOwnerAddress);
            }


        }
    };

    public WifiP2pManager.PeerListListener returnPeerListListener()
    {
        return myPeerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Toast.makeText(context, "Wifi p2p is enabled.", Toast.LENGTH_SHORT).show();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            Toast.makeText(context, "Wifi p2p is enabled.", Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null)
            {
                mManager.requestPeers(mChannel, peerListListener);

                if (peers.size() > 0)
                {
                    final  WifiP2pDevice device = peers.get(0);
                    if (!connected_devices.contains(device.deviceAddress)) {
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                        config.wps.setup = WpsInfo.PBC;
                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                //success logic
                                String msg = "I have connected with ";
                                msg += device.deviceName;
                                msg += " with MAC: ";
                                msg += device.deviceAddress;
                                msg += "\n";
                                activity.display_message(msg);
                                connected_devices.add(device.deviceAddress);
                            }

                            @Override
                            public void onFailure(int reason) {
                                //failure logic
                                activity.display_message("I have failed to connected!\n");
                            }
                        });
                    }
                }
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            activity.display_message("UHJGHGHGHHGG1!\n");
            if(networkState.isConnected())
            {
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            }
            else
            {
                activity.display_message("UHJGHGHGHHGG2!\n");
                connected_devices.clear();
            }

            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            // Respond to this device's wifi state changing
        }
    }
}