package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class conversation extends AppCompatActivity {
    String username, buddyName;
    ListView convolistView;
    Context context;
    public static List<Message> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("UserName", null);
        messages = new ArrayList<>();
        convolistView = (ListView)findViewById(R.id.convList);
        context = getApplicationContext();

        //load messageLogs
        readConvLog("blank.txt");

        //populate messages
        LiveUpdateConversaition();
    }

    public void LiveUpdateConversaition(){
        ArrayAdapter<Message> convAdapater = new convListAdapter(context);
        convolistView.setAdapter(convAdapater);
    }

    private void readConvLog(String filename){
        filename = "";
        addMsg();

    }

    private void addMsg(){
        Message msg = new Message("Stephanie", "2123123", "hello ryota, you baka\n KONOYAROU!\n DOHENTAI", "2/30/16");
        messages.add(msg);
        LiveUpdateConversaition();
    }

    private class convListAdapter extends ArrayAdapter<Message> {
        public convListAdapter(Context context) {
            super(context, R.layout.listview_bubble_left, messages);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            // if there's no instance of this view
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.listview_bubble_left, parent, false);
            }

            Message currentMsg = messages.get(position);
            TextView body = (TextView) view.findViewById(R.id.msgBody);
            body.setText(currentMsg.getMessageBody());

            TextView date = (TextView) view.findViewById(R.id.txtDate);
            date.setText(currentMsg.getTimeStamp());

//            ImageView pic = (ImageView) view.findViewById(R.id.ProfilePic);
//            Drawable myDrawable = getResources().getDrawable(R.drawable.profile_gray);
//            pic.setImageDrawable(myDrawable);
            return view;
        }
    }

}
