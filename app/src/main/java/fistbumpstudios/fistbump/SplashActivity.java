package fistbumpstudios.fistbump;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    ImageView splash_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        splash_iv = (ImageView) findViewById(R.id.splash_iv);

        AnimationDrawable animation = (AnimationDrawable) splash_iv.getBackground();
        animation.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, tabbedMain.class);
        startActivity(intent);
        finish();
    }
}

