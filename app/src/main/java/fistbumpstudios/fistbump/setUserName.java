package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class setUserName extends AppCompatActivity {
    File userInfo = null;
    final public static String USERFILENAME = "user_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
    }

    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress() + "\n";
    }

    private void makeVerifyFile(String username){

        try {
            boolean success = true;
            FileOutputStream fos = openFileOutput(USERFILENAME, Context.MODE_PRIVATE);
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
