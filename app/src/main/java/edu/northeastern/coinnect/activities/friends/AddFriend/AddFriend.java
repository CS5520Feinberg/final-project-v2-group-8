package edu.northeastern.coinnect.activities.friends.AddFriend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.CompletableFuture;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.repositories.UsersRepository;

public class AddFriend extends AppCompatActivity {

    private Button searchButton;
    private TextInputEditText searchFriendInput;

    private FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();
    private UsersRepository usersRepository = UsersRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersRepository.setCurrentUserName(firebaseDBHandler.getCurrentUserName());
        setContentView(R.layout.activity_add_friend);
        searchButton = findViewById(R.id.addFriendBtn);
        searchFriendInput = findViewById(R.id.friendEditText);

        searchButton.setOnClickListener(v -> {
            CompletableFuture<Boolean> isUser = usersRepository.isUser(searchFriendInput.getText().toString());

            isUser.thenAccept(ans -> {
                if (ans) {
                    Log.d("USER", "User found");
                    Toast.makeText(getApplicationContext(), "User Exists", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("USER", "No user found");
                    Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_LONG).show();
                }
            });
        });


    }

}