package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get views for animation
        TextView appName = findViewById(R.id.appName);


        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        // Apply animations
        appName.startAnimation(slideUp);

        // Using Handler to delay the transition to LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start LoginActivity after splash delay
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close splash activity so user can't go back to it
            }
        }, SPLASH_DELAY);
    }
}