package edu.northeastern.coinnect.activities.friends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.AddFriend.AddFriend;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.databinding.ActivityFriendsBinding;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.activities.friends.FriendRecyclerView.FriendRecyclerViewAdapter;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FriendsActivity extends AppCompatActivity {

  private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();
  private int ADD_FRIEND_REQUEST_CODE = 1;

  private RecyclerView friendRecyclerView;
  private final UsersRepository usersRepository = UsersRepository.getInstance();
  private FriendRecyclerViewAdapter friendRecyclerViewAdapter;

  private edu.northeastern.coinnect.databinding.ActivityFriendsBinding binding;
  private String currentUser;
  private List<String> friendList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    currentUser = this.usersRepository.getCurrentUserName();
    firebaseDBHandler.setCurrentUserName(currentUser);
    binding = ActivityFriendsBinding.inflate(getLayoutInflater());
    FloatingActionButton addFriendFAB = binding.floatingActionButtonAddFriend;

    addFriendFAB.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), AddFriend.class);
      startActivityForResult(intent, ADD_FRIEND_REQUEST_CODE);

    });
    View view = binding.getRoot();
    setContentView(view);
    friendList = usersRepository.getCurrentUserFriends();
    Collections.sort(friendList);
    setupRecyclerView();

    BottomNavigationView navView = binding.bottomNavFriends;
    navView.setSelectedItemId(R.id.friendsActivity);
    this.setupNavBarActions(navView);
  }

  private void setupRecyclerView() {

    friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(friendList);
    friendRecyclerView = binding.friendsRecyclerView;
    friendRecyclerView.setHasFixedSize(true);
    friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    friendRecyclerView.setAdapter(friendRecyclerViewAdapter);
  }


  protected void setupNavBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          Intent intent;
          if (item.getItemId() == R.id.homeActivity) {
            intent = new Intent(this, HomeActivity.class);;
          } else if (item.getItemId() == R.id.transactionActivity) {
            intent = new Intent(this, TransactionsActivity.class);
          } else if (item.getItemId() == R.id.friendsActivity) {
            return true;
          } else if (item.getItemId() == R.id.settingsActivity) {
            intent = new Intent(this, SettingsActivity.class);
          } else {
            return false;
          }

          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          overridePendingTransition(0, 0);
          finish();
          return true;
        });
  }

  /**
   * This method is what saves the state of RecyclerView on screen rotation.
   *
   * @param state Bundle in which to place your saved state.
   */
  @Override
  protected void onSaveInstanceState(@NonNull Bundle state) {
    super.onSaveInstanceState(state);

    if (this.friendRecyclerView.getLayoutManager() != null) {
      Parcelable recyclerState = this.friendRecyclerView.getLayoutManager().onSaveInstanceState();
      state.putParcelable("recyclerState", recyclerState);
    }
  }

  /**
   * This method is what restores the saved state of recycler view on screen rotation.
   *
   * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
   */
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (this.friendRecyclerView.getLayoutManager() != null) {
      Parcelable recyclerState = savedInstanceState.getParcelable("recyclerState");
      this.friendRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ADD_FRIEND_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

      Log.d("CURRENT FRIENDS", String.valueOf(usersRepository.getCurrentUserFriends()));
      String newFriend = data.getStringExtra("add_friend_data");
      friendList.add(newFriend);
      setupRecyclerView();
    }
  }
}
