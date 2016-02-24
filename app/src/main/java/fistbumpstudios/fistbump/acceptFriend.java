package fistbumpstudios.fistbump;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class acceptFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friend);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri beamUri = intent.getData();
            Toast.makeText(this, "Received NFC" , Toast.LENGTH_LONG).show();
        }
        //open this activity when a friend request is recived by android beam!
    }


}
