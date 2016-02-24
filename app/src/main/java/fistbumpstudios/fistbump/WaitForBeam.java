package fistbumpstudios.fistbump;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class WaitForBeam extends AppCompatActivity {
    NfcAdapter nfc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_beam);
        File dir = new File(Environment.getExternalStorageDirectory(), "FistBump");
        File file = new File(dir, "user_info.txt");

//        try {
//            FileInputStream in = openFileInput("user_info.txt");
//            InputStreamReader isr = new InputStreamReader(in);
//            BufferedReader buffreader = new BufferedReader(isr);
//            String readString = buffreader.readLine();
//            Toast.makeText(this, readString, Toast.LENGTH_LONG).show();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
//
//        nfc.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback()
//        {
//
//            @Override
//            public NdefMessage createNdefMessage(NfcEvent event)
//            {
//                NdefRecord uriRecord = NdefRecord.createUri(Uri.encode("readString"));
//                return new NdefMessage(new NdefRecord[] { uriRecord });
//            }
//        }, this, this);
//    }

        nfc = NfcAdapter.getDefaultAdapter(this);
        file.setReadable(true, false);
        nfc.setBeamPushUris(new Uri[]{Uri.fromFile(file)}, this);
    }

    public void goBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
