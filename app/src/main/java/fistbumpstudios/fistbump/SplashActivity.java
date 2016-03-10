package fistbumpstudios.fistbump;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

public class SplashActivity extends Activity {

    ImageView splash_iv;
    String[] splashMsgs = {
            "Readying consecutive normal punches..",
            "ONEEE PAAWWWNNCCHHHHH!",
            "Bro fist, dude!",
            "Whales.",
            "FALCON PUNCH!",
            "Hello World.",
            "Don't break your phones! :)",
            "Totally not inspired by Discord.",
            "Your will, my hands."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ImageView img = (ImageView)findViewById(R.id.splash_iv);
        img.setBackgroundResource(R.drawable.anim);

        int min = 0;
        int max = splashMsgs.length;

        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int randomNum = r.nextInt(max - 0 + 1) + min;
        String text = splashMsgs[randomNum];

        Typeface font = Typeface.createFromAsset(getAssets(), "BuxtonSketch.ttf");
        TextView splashText = (TextView) findViewById(R.id.splash_tv);
        splashText.setText(text);
        splashText.setTypeface(font);

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


