package fistbumpstudios.fistbump;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class buddiesTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    boolean BuddylistChanged;
    private ListView mListView;
    public static List<Buddy> Buddies;
    ListView buddylistView;
    TextView emptyListView;


    public buddiesTab() {
        // Required empty public constructor
    }


    public static buddiesTab newInstance(String param1, String param2) {
        buddiesTab fragment = new buddiesTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

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
    }


    private void LoadBuddies()
    {
        try {
            String Message;
            FileInputStream fis = getActivity().openFileInput(acceptFriend.friendFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();

            while ( (Message = br.readLine()) != null)
            {
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
        Toast.makeText(getContext(), obj.getString("MACAddress"), Toast.LENGTH_LONG).show();
    }

    // Implementation question: Should we update the database right after adding?
    private void addBuddy(String name, String id, Uri pic)
    {
        Buddies.add(new Buddy(name, id, pic));
        LiveUpdateBuddies();
    }


    // update buddies (while in App)
    private void LiveUpdateBuddies()
    {
        ArrayAdapter<Buddy> buddyAdapter = new BuddyListAdapter(getActivity());
        buddylistView.setAdapter(buddyAdapter);
    }


    private class BuddyListAdapter  extends ArrayAdapter<Buddy>
    {
        public BuddyListAdapter(Context context)
        {
            super(context,R.layout.listview_buddy_info, Buddies);
        }

        @Override
        public View getView (int position, View view, ViewGroup parent)
        {

            // if there's no instance of this view
            if (view == null){
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


            ImageView pic = (ImageView) view.findViewById(R.id.ProfilePic);
            Drawable myDrawable = getResources().getDrawable(R.drawable.profile_gray);
            pic.setImageDrawable(myDrawable);
                        //pic.setImageURI(currentBuddy.getProficPic());
            return view;
        }
    }
}