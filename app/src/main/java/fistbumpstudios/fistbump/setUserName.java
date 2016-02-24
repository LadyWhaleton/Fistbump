package fistbumpstudios.fistbump;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class setUserName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress() + "\n";
    }

    private void makeVerifyFile(String username){

        try {
            boolean success = true;
            //FileOutputStream fos = openFileOutput("user_info.txt",Context.MODE_WORLD_READABLE);

            File dir = new File(Environment.getExternalStorageDirectory(), "FistBump");
            File file = new File(dir, "user_info.txt");
            FileOutputStream fos = new FileOutputStream(file);
            username = username +'\n';
            fos.write(username.getBytes());
            fos.write(getMAC().getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUname (View view){

        EditText mEdit=(EditText)findViewById(R.id.username);
        String uname = mEdit.getText().toString();

        if(uname.equals("")){
            Toast.makeText(this, "Enter a username!" + uname,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Set Username to " + uname ,
                Toast.LENGTH_SHORT).show();
        makeVerifyFile(uname);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
