package ryota.nfctestapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;



public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    String username = "RyotaSaito";
    String filename = username + ".txt";
    File userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
            makeVerifyFile();
        }
    }


    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress() + "\n";
    }

    private void makeVerifyFile(){
        try {
            File directory = new File(Environment.getExternalStorageDirectory(),"FistBump");
            //Toast.makeText(this,Environment.getExternalStorageDirectory(),Toast.LENGTH_LONG).show();

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
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
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
        }
        else {
            userInfo.setReadable(true, false);
            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(userInfo)}, this);
        }


    }
}
