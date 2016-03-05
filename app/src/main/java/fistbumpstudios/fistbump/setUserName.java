package fistbumpstudios.fistbump;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class setUserName extends AppCompatActivity {


    final public static String userFilename = "userInfo.txt";
    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    private void makeVerifyFile(String username){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserName", username);
        editor.putString("MAC", getMAC());
        editor.apply();

        try {
            FileOutputStream fos = openFileOutput(userFilename, Context.MODE_PRIVATE);
            fos.write((username + "\n").getBytes());
            fos.write((getMAC()+"\n").getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void verifyUsername(View view){

        EditText mEdit=(EditText)findViewById(R.id.username);
        String uname = mEdit.getText().toString();

        if(uname.equals("")){
            Toast.makeText(this, "Enter a username!" + uname,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        makeVerifyFile(uname);
        Intent intent = new Intent(this, setProfilePic.class);
        startActivity(intent);
        finish();
    }

}
