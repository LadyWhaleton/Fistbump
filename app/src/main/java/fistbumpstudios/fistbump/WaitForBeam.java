package fistbumpstudios.fistbump;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WaitForBeam extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{
    NfcAdapter nfc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_beam);
        nfc = NfcAdapter.getDefaultAdapter(this);
        nfc.setNdefPushMessageCallback(this, this);
    }
    public void goBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "";
        File userfile = new File("user_info.txt");
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(userfile));
            String line;
            while ((line = br.readLine()) != null) {
                message += line +";";
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "cannot find userinfo" , Toast.LENGTH_LONG).show();
        }
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }
}
