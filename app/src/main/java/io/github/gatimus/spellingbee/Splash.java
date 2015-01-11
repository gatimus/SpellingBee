package io.github.gatimus.spellingbee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Splash extends Activity {

    private static final String TAG = "Splash:";
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Create");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Intent Main");
                Intent i = new Intent(Splash.this, Main.class);
                startActivity(i);
                finish();
            } //run
        },SPLASH_TIME_OUT);
    } //onCreate

} //class