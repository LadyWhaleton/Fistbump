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
        return WifiDirect.p2p_mac_address;
    }

    private void makeVerifyFile(String username){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserName", username);
        editor.putString("MAC", getMAC());
        editor.apply();

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
