package fistbumpstudios.fistbump;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class acceptFriend extends AppCompatActivity {


    String infoRaw;
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
        }
    }

    public void accept(View view) throws JSONException, IOException {
        String[] infoArray =  infoRaw.split(";");
        JSONObject obj = new JSONObject();
        obj.put("name", infoArray[0]);
        obj.put("MAC Address", infoArray[1]);
        //create new json object and save to filesystem

        File dir = new File(Environment.getExternalStorageDirectory(), "FistBump");
        File userfile = new File(dir, "friends.txt");
        FileWriter writer = new FileWriter(userfile, true);

        Toast.makeText(this, obj.toString(), Toast.LENGTH_LONG).show();


    }

    public void reject(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        //reject friend and open MainActivity

    }


}
