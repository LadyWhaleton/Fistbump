package fistbumpstudios.fistbump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    String username, filename;
    File userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("uname") ;
        filename = username+".txt";
        checkBeam();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
    }


    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress() + "\n";
    }

    private  void checkBeam(){
        PackageManager pm = this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void makeVerifyFile(){

        try {
            File directory = new File(Environment.getExternalStorageDirectory(),"FistBump");
            boolean success = true;

            if (!directory.exists()) {
                success = directory.mkdirs();
            }
            if (success) {
                Toast.makeText(this,directory.getAbsolutePath() + " Created!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,directory.getAbsolutePath() + " Failed!",
                        Toast.LENGTH_LONG).show();
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

    public void sendFile(View view) {
        if(username.equals("")){
            Toast.makeText(this, "Enter a username first!" + username ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        makeVerifyFile();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if (!nfcAdapter.isEnabled()) {
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if (!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        } else {
            userInfo.setReadable(true, false);
            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(userInfo)}, this);
        }
    }
    public void setUsername(View view){

        EditText mEdit   = (EditText)findViewById(R.id.username);
        username = mEdit.getText().toString();

        if(username.equals("")){
            Toast.makeText(this, "Enter a username!" + username ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        filename = username + ".txt";
        Toast.makeText(this, "Set Username to " + username ,
                Toast.LENGTH_SHORT).show();
    }

}
