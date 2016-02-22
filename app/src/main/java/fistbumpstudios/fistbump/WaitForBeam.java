package fistbumpstudios.fistbump;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class WaitForBeam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_beam);

        try {
            FileInputStream in = openFileInput(setUserName.USERFILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
