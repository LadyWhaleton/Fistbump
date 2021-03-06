package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class acceptFriend extends AppCompatActivity {
    String infoRaw;
    String[] infoArray;
    final public static String friendFile = "friends.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friend);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred
            infoRaw = new String(message.getRecords()[0].getPayload());
            infoArray =  infoRaw.split(";");

            TextView friendName = (TextView) findViewById(R.id.textview_acceptFriend);
            String text = "Accept friend request from " + infoArray[0] + "?";
            friendName.setText(text);

        }
    }

    public  void add_friend(String name, String mac) throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("MACAddress", mac);
        //create new json object and save to filesystem
        FileOutputStream fos = openFileOutput(friendFile, Context.MODE_PRIVATE | MODE_APPEND);
        OutputStreamWriter out = new OutputStreamWriter(fos);
        out.append(obj.toString());
        out.append(System.getProperty("line.separator"));
        out.flush();

    }

    public void accept(View view) throws JSONException, IOException {
        JSONObject obj = new JSONObject();
        obj.put("name", infoArray[0]);
        obj.put("MACAddress", infoArray[1]);

        //create new json object and save to filesystem
        FileOutputStream fos = openFileOutput(friendFile, Context.MODE_PRIVATE | MODE_APPEND);
        OutputStreamWriter out = new OutputStreamWriter(fos);
        out.append(obj.toString());
        out.append(System.getProperty("line.separator"));
        out.flush();

        //open main activity after finishing writing your friend
        Intent intent = new Intent(this, tabbedMain.class);
        intent.putExtra("connect", "yes");

        startActivity(intent);
        finish();
    }

    public void reject(View view){
        Intent intent = new Intent(this, tabbedMain.class);
        startActivity(intent);
        finish();
    }
}