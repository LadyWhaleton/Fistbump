package fistbumpstudios.fistbump;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TabbedMain extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    NfcAdapter nfc;
    Context context;

    private int[] tabIcons = {
            R.drawable.ic_contacts_pink_24dp,
            R.drawable.ic_chat_pink_24dp,
            R.drawable.ic_perm_media_pink_24dp
    };
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_main);
        context = getApplicationContext();

        //check if user already made a profile
        if (checkNewUser()) {
            //if new user, then open setusername activity
            Intent intent = new Intent(this, setUserName.class);
            startActivity(intent);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // add the tab icons
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        nfc = NfcAdapter.getDefaultAdapter(this);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForNFC())
                    nfc.setNdefPushMessageCallback(TabbedMain.this, TabbedMain.this);
            }
        });
    }

    private boolean checkNewUser() {
        String path = context.getFilesDir().getAbsolutePath() + "/" + setUserName.userFilename;
        File file = new File(path);
        return !file.exists();
    }

    public boolean checkForNFC() {
        if (nfc == null) {
            Toast.makeText(this, "No NFC available",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!nfc.isEnabled()) {
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        } else if (!nfc.isNdefPushEnabled()) {
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_main, menu);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new BuddiesTab();
                case 1:
                    return new MessagesTab();
                case 2:
                    return new MediaGalleryTab();
                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.title_tab_section1);
                case 1:
                    return getString(R.string.title_tab_section2);
                case 2:
                    return getString(R.string.title_tab_section3);
            }
            return null;
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String message = "";
        String filepath = getApplicationContext().getFilesDir() + "/" + setUserName.userFilename;
        File userfile = new File(filepath);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(userfile));
            String line;
            while ((line = br.readLine()) != null) {
                message += line + ";";
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "cannot find userinfo", Toast.LENGTH_LONG).show();
        }
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }
}
