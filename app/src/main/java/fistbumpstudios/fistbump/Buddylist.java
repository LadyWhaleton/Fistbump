package fistbumpstudios.fistbump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Buddylist extends AppCompatActivity {

    List<Buddy> bl = new ListArray<Buddy>;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddylist);
    }
}
