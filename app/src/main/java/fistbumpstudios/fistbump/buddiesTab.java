package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class buddiesTab extends android.support.v4.app.Fragment {
    public static List<Buddy> Buddies;
    ListView buddylistView;
    TextView emptyListView;

    String buddyname, macAddr;
    public ArrayAdapter<Buddy> buddyAdapter;

    public buddiesTab() {
    }

    public static buddiesTab newInstance() {
        buddiesTab fragment = new buddiesTab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_buddylist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Buddies = new ArrayList<>();
        buddylistView = (ListView) getView().findViewById(R.id.buddylistView);
        emptyListView = (TextView) getView().findViewById(R.id.emptyBuddylistView);
        buddylistView.setEmptyView(emptyListView);
        LoadBuddies();
        setListClickListener();
    }

    private void setListClickListener(){
        buddylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                buddyname = ((TextView) v.findViewById(R.id.BuddyName)).getText().toString();
                macAddr = ((TextView) v.findViewById(R.id.MacAddress)).getText().toString();
                registerForContextMenu(buddylistView);
                buddylistView.showContextMenuForChild(v);
                unregisterForContextMenu(buddylistView);

            }
        });
    }
    private void LoadBuddies() {
        try {
            String Message;
            FileInputStream fis = getActivity().openFileInput(acceptFriend.friendFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();

            while ((Message = br.readLine()) != null) {
                JSONObject obj = new JSONObject(Message);
                jsonToBuddy(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LiveUpdateBuddies();
    }

    private void jsonToBuddy(JSONObject obj) throws JSONException {
        Uri profilePic = Uri.parse("http://orig06.deviantart.net/1722/f/2009/346/0/d/wailord_by_xous54.png");
        addBuddy(obj.getString("name"), obj.getString("MACAddress"), profilePic);
    }

    // Implementation question: Should we update the database right after adding?
    public void addBuddy(String name, String id, Uri pic) {
        Buddies.add(new Buddy(name, id, pic));
        LiveUpdateBuddies();
    }

    // update buddies (while in App)
    public void LiveUpdateBuddies() {
        buddyAdapter = new BuddyListAdapter(getActivity());
        buddylistView.setAdapter(buddyAdapter);
    }

    public void reloadBuddies() {

    }

    private class BuddyListAdapter extends ArrayAdapter<Buddy> {
        public BuddyListAdapter(Context context) {
            super(context, R.layout.listview_buddy_info, Buddies);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            // if there's no instance of this view
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.listview_buddy_info, parent, false);
            }

            Buddy currentBuddy = Buddies.get(position);

            // select the TextView variable to be modified
            TextView name = (TextView) view.findViewById(R.id.BuddyName);

            // set the actual TextView variable to the Buddy's name
            name.setText(currentBuddy.getName());

            // do the same for the other fields
            TextView macAddr = (TextView) view.findViewById(R.id.MacAddress);
            macAddr.setText(currentBuddy.getID());
            TextView status = (TextView) view.findViewById(R.id.Status);
            if (currentBuddy.isOnline())
                status.setText("Online");
            else
                status.setText("Offline");

            File imgFile = new File(Environment.getExternalStorageDirectory() + "/FistBump/ProfilePics/" + currentBuddy.getID() + ".jpg");
            if (!imgFile.exists())
                imgFile = new File(Environment.getExternalStorageDirectory() + "/FistBump/ProfilePics/" + currentBuddy.getID() + ".png");

            if(imgFile.exists()){

                Bitmap myBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgFile.getAbsolutePath()), 256, 256);

                ImageView pic = (ImageView) view.findViewById(R.id.ProfilePic);

                pic.setImageBitmap(myBitmap);

            }

            //Drawable myDrawable = getResources().getDrawable(R.drawable.profile_gray);

            //pic.setImageDrawable(myDrawable);
            return view;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.buddylistView) {
            menu.setHeaderTitle("Select an Action");
            menu.add(0, v.getId(), 0, "Connect");
            menu.add(0, v.getId(), 0, "Send Message");
            menu.add(0, v.getId(), 0, "Edit");
            menu.add(0, v.getId(), 0, "Delete");
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Connect")) {

            if (!WiFiDirectBroadcastReceiver.connected_devices.contains(macAddr)) {
                //Toast.makeText(getContext(), "Connect!", Toast.LENGTH_SHORT).show();
                WifiDirect.friend_mac_addr = new String(macAddr);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = macAddr;
                config.wps.setup = WpsInfo.PBC;
                WifiDirect.mManager.connect(WifiDirect.mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //success logic
                        String msg = "I have connected with ";
                        msg += buddyname;
                        msg += " with MAC: ";
                        msg += macAddr;
                        msg += "\n";
                        WifiDirect.display_message(msg);
                    }

                    @Override
                    public void onFailure(int reason) {
                        //failure logic
                        //activity.display_message("I have failed to connected!\n");
                    }
                });
            }
        }
        else if (item.getTitle().equals("Send Message")) {
            Intent intent = new Intent(getActivity() , conversation.class);
            intent.putExtra("name", buddyname);
            intent.putExtra("id", macAddr);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        else if (item.getTitle().equals("Edit")) {
            Toast.makeText(getContext(), "Editing", Toast.LENGTH_SHORT).show();
        }
        else if (item.getTitle().equals("Delete")) {
            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
        }
        else {
            return false;
        }
        return true;
    }


}
