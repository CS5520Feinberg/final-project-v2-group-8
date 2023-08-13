package edu.northeastern.coinnect.activities.welcome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.ZoneId;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class WelcomeActivity extends AppCompatActivity {

  private final static UsersRepository userRepository = UsersRepository.getInstance();
  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    // getting day of month for user
    // </editor-fold>
    // grabbing the welcome message and and setting the animation from @anim/slide_up_fade_in
    TextView welcomeMsg = findViewById(R.id.helloText);
    Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
        R.anim.slide_up_fade_in);

    // setting user's name & kicking off welcome message.
    String userName = userRepository.getCurrentUserName();
    welcomeMsg.setText("Hello " + userName);
    welcomeMsg.startAnimation(fadeIn);
    // Delaying to show loading screen.
    new Handler().postDelayed(() -> {
      Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
      startActivity(intent);
      finish();
    }, 3000); // have this showing for 3 sec as of now, but maybe we can make shorter?
  }
}