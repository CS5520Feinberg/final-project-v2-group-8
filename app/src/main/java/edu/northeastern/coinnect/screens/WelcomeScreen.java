package edu.northeastern.coinnect.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import edu.northeastern.coinnect.R;

public class WelcomeScreen extends AppCompatActivity {

  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    // grabbing the welcome message and and setting the animation from @anim/slide_up_fade_in
    TextView welcomeMsg = findViewById(R.id.helloText);
    Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_up_fade_in);

    // setting user's name & kicking off welcome message.
    String userName = getIntent().getStringExtra("USER_NAME");
    welcomeMsg.setText("Hello,\n " + userName);
    welcomeMsg.startAnimation(fadeIn);

    // Delaying to show loading screen.
    new Handler().postDelayed(() -> {
      Intent intent = new Intent(WelcomeScreen.this, HomeScreen.class);
      startActivity(intent);
      finish();
    }, 3000); // have this showing for 3 sec as of now, but maybe we can make shorter?
  }
}