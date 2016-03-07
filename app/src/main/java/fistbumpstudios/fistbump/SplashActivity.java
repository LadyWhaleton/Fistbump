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
        ImageView img = (ImageView)findViewById(R.id.splash_iv);
        img.setBackgroundResource(R.drawable.anim);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this, tabbedMain.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }



}


