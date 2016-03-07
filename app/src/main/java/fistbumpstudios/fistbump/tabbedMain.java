package fistbumpstudios.fistbump;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class tabbedMain extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Thread peer_discovery_thread = null;
    private Object peer_discover_lock;
    private boolean peer_discover_flag;
    public static String userName;
    NfcAdapter nfc;
    static Context context;

    public static buddiesTab buddiesTabFragment;

    private int[] tabIcons = {
            R.drawable.ic_contacts_pink_24dp,
            R.drawable.ic_chat_pink_24dp,
            R.drawable.ic_perm_media_pink_24dp
    };
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_main);
        context = getApplicationContext();

        //check if user already made a profile
        if(checkNewUser())
        {
            //if new user, then open setusername activity
            Intent intent = new Intent(this, setUserName.class);
            startActivity(intent);
            finish();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userName = preferences.getString("UserName", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //make dialog

        final Dialog settingsDialog = new Dialog(tabbedMain.this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.activity_wait_for_beam
                , null));


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // add the tab icons
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        WifiDirect.mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiDirect.mChannel = WifiDirect.mManager.initialize(this, getMainLooper(), null);
        WifiDirect.mReceiver = new WiFiDirectBroadcastReceiver(WifiDirect.mManager, WifiDirect.mChannel, this);

        WifiDirect.mIntentFilter = new IntentFilter();
        WifiDirect.mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        WifiDirect.mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        WifiDirect.mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        WifiDirect.mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        peer_discover_lock = new Object();
        peer_discover_flag = true;

        peer_discovery_thread = new Thread(new Peer_discovery_thread());
        peer_discovery_thread.start();

        nfc = NfcAdapter.getDefaultAdapter(this);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(checkForNFC()){
                nfc.setNdefPushMessageCallback(tabbedMain.this, tabbedMain.this);
                //Toast.makeText(context, "tap another phone!" , Toast.LENGTH_SHORT).show();
                settingsDialog.show();

            }}
        });
    }

    private boolean checkNewUser(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("UserName", null);
        String pic = preferences.getString("profilePic", null);
        Toast.makeText(this, name+pic, Toast.LENGTH_SHORT).show();
        if( (name != null) && (pic != null) ){
            return false;
        }

        return true;
    }

    public boolean checkForNFC() {
        if( nfc == null){
            Toast.makeText(this, "No NFC available",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!nfc.isEnabled()) {
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        } else if (!nfc.isNdefPushEnabled()) {
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position)
            {
                case 0:
                    buddiesTabFragment = new buddiesTab();
                    return buddiesTabFragment;
                case 1:
                    return new messagesTab();
                case 2:
                    return new MediaGalleryTab();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.title_tab_section1);
                case 1:
                    return getString(R.string.title_tab_section2);
                case 2:
                    return getString(R.string.title_tab_section3);
            }
            return null;
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "";
        String filepath =  getApplicationContext().getFilesDir() + "/" + setUserName.userFilename;
        File userfile = new File(filepath);
        BufferedReader br = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("UserName", null);
        String mac = preferences.getString("MAC", null);
        message = username + ";" + mac + ";";


        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(WifiDirect.mReceiver, WifiDirect.mIntentFilter);
        synchronized (peer_discover_lock) {
            peer_discover_flag = true;
            peer_discover_lock.notifyAll();
        }
    }

    // unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        synchronized (peer_discover_lock) {
            peer_discover_flag = false;
        }
        unregisterReceiver(WifiDirect.mReceiver);
    }

    public static void add_friend(String name, String mac) throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("MACAddress", mac);

        //create new json object and save to filesystem
        FileOutputStream fos = context.openFileOutput(acceptFriend.friendFile, MODE_PRIVATE | MODE_APPEND);
        OutputStreamWriter out = new OutputStreamWriter(fos);
        out.append(obj.toString());
        out.append(System.getProperty("line.separator"));
        out.flush();
        buddiesTab.Buddies.add(new Buddy(name, mac, null));

        //buddiesTabFragment.addBuddy(name, mac, null);
        //buddiesTabFragment.buddyAdapter.notifyDataSetChanged();
        //buddiesTabFragment.buddylistView.invalidateViews();

    }

    class Peer_discovery_thread implements Runnable {

        public void run() {
            while (true){

                try {
                    WifiDirect.mManager.discoverPeers(WifiDirect.mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            //WifiDirect.display_message("Finding people to connect to!");

                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            //WifiDirect.display_message("Woopsydasicals!");
                        }
                    });
                    Thread.sleep(10000);
                    synchronized (peer_discover_lock) {
                        while (!peer_discover_flag) {
                            try {
                                peer_discover_lock.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
