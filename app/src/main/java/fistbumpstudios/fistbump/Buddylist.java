package fistbumpstudios.fistbump;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Buddylist extends AppCompatActivity {

    //bool BuddyListChanged; // indicates whether the buddylist has been modified during the session
    EditText nameTxt, macTxt;
    List<Buddy> Buddies;
    ListView buddylistView;

    // This function is called upon startup of the app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddylist);
        Buddies = new ArrayList<>();
        buddylistView = (ListView) findViewById(R.id.listView);
        LoadBuddies();


    }

    // Load Buddies from internal device storage
    // Note: We will need to create our on parsing algorithm.
    private void LoadBuddies()
    {
        try {
            String Message;
            FileInputStream fis = openFileInput(acceptFriend.friendFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();

            while ( (Message = br.readLine()) != null)
            {
                JSONObject obj = new JSONObject(Message);
                jsonToBuddy(obj);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Json is unreadable!", Toast.LENGTH_LONG).show();
        }

        LiveUpdateBuddies();
    }

    private void jsonToBuddy(JSONObject obj) throws JSONException {
        Uri profilePic = Uri.parse("http://orig06.deviantart.net/1722/f/2009/346/0/d/wailord_by_xous54.png");
        addBuddy(obj.getString("name"), obj.getString("MACAddress"), profilePic);
        //Toast.makeText(this,  obj.getString("MACAddress"), Toast.LENGTH_LONG).show();
    }


    // update buddies (while in App)
    private void LiveUpdateBuddies()
    {
        ArrayAdapter<Buddy> buddyAdapter = new BuddyListAdapter();
        buddylistView.setAdapter(buddyAdapter);
    }

    // Implementation question: Should we update the database right after adding?
    private void addBuddy(String name, String id, Uri pic)
    {
        Buddies.add(new Buddy (name, id, pic));
    }

    private class BuddyListAdapter  extends ArrayAdapter<Buddy>
    {
        public BuddyListAdapter()
        {
            super (Buddylist.this, R.layout.listview_buddy_info, Buddies);
        }

        @Override
        public View getView (int position, View view, ViewGroup parent)
        {
            // if there's no instance of this view
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_buddy_info, parent, false);

            Buddy currentBuddy = Buddies.get(position);

            // select the TextView variable to be modified
            TextView name = (TextView) view.findViewById(R.id.BuddyName);

            // set the actual TextView variable to the Buddy's name
            name.setText(currentBuddy.getName());

            // do the same for the other fields
            TextView macAddr = (TextView) view.findViewById(R.id.MacAddress);
            macAddr.setText(currentBuddy.getID());

            // Profile pic
            ImageView pic = (ImageView) view.findViewById(R.id.ProfilePic);
            pic.setImageURI(currentBuddy.getProficPic());

            return view;
        }
    }
}