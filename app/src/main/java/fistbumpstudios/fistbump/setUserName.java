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
    String uname = "";
    String fname = "";
    File userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_name);
    }

    public void setUname (View view){

        EditText mEdit=(EditText)findViewById(R.id.username);
        uname = mEdit.getText().toString();

        if(uname.equals("")){
            Toast.makeText(this, "Enter a username!" + uname,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Set Username to " + uname ,
                Toast.LENGTH_SHORT).show();

        makeVerifyFile(uname);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("uname", uname);
        startActivity(intent);
        finish();
    }

    private void makeVerifyFile(String username) {

        String filename = username + ".txt";
        try {
            File directory = new File(Environment.getExternalStorageDirectory(), "FistBump");
            boolean success = true;

            if (!directory.exists()) {
                success = directory.mkdirs();
            }
            if (success) {
                Toast.makeText(this, directory.getAbsolutePath() + " Created!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, directory.getAbsolutePath() + " Failed!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            userInfo = new File(directory, filename);
            FileOutputStream fos = new FileOutputStream(userInfo);
            OutputStreamWriter out = new OutputStreamWriter(fos);

            out.write(username + "\n");
            out.write(getMAC());

            out.write('\n');
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress() + "\n";
    }

}
