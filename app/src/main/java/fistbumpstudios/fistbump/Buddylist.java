package fistbumpstudios.fistbump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Buddylist extends AppCompatActivity {

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

    }

    private void loadBuddies()
    {
        ArrayAdapter<Buddy> buddyAdapter = new BuddyListAdapter();
        buddylistView.setAdapter(buddyAdapter);
    }

    private void addBuddy(String name, String id)
    {
        Buddies.add(new Buddy (name, id));
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

            return view;
        }
    }
}
