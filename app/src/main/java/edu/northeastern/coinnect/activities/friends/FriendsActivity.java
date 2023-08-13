package edu.northeastern.coinnect.activities.friends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.databinding.ActivityFriendsBinding;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView.FriendListCallback;
import edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView.FriendRecyclerViewAdapter;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendsActivity extends AppCompatActivity {

  private final Handler handler = new Handler();
  private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();

  private FriendRecyclerViewAdapter friendRecyclerViewAdapter;
  private RecyclerView friendRecyclerView;
  private final UsersRepository usersRepository = UsersRepository.getInstance();
  private String currentUser;

  private ArrayList<String> friendList = new ArrayList<>();

  private edu.northeastern.coinnect.databinding.ActivityFriendsBinding binding;

  private void setupRecyclerView(ActivityFriendsBinding binding, ArrayList<String> listOfFriends) {

    friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(listOfFriends);
    friendRecyclerView = binding.friendsRecyclerView;
    friendRecyclerView.setHasFixedSize(true);
    friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    friendRecyclerView.setAdapter(friendRecyclerViewAdapter);
    friendRecyclerViewAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityFriendsBinding.inflate(getLayoutInflater());

    View view = binding.getRoot();
    setContentView(view);
    currentUser = this.usersRepository.getCurrentUserName();

    friendList = loadFriendList(currentUser, friendList -> setupRecyclerView(binding, friendList));

    BottomNavigationView navView = binding.bottomNavFriends;
    navView.setSelectedItemId(R.id.friendsActivity);
    menuBarActions(navView);
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.homeActivity) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

          } else if (item.getItemId() == R.id.friendsActivity) {
            return true;
          } else if (item.getItemId() == R.id.transactionActivity) {
            Intent intent = new Intent(this, TransactionsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          }
          return false;
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

  private ArrayList<String> loadFriendList(String currUser, FriendListCallback callback) {

    ArrayList<String> getList = new ArrayList<>();

    firebaseDBHandler
        .getDbInstance()
        .getReference()
        .child("USERS/" + currUser)
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                HashMap<String, Object> value =
                    (HashMap<String, Object>) task.getResult().getValue();
                if (value.containsKey("friends")) {
                  ArrayList<String> friends = (ArrayList<String>) value.get("friends");

                  // Remove "[" and "]" characters from each friend and add to getList
                  // ** this is so clunky but all I could figure out to get rid of brackets **
                  for (String friend : friends) {
                    getList.add(friend.replaceAll("[\\[\\]]", ""));
                  }
                }
                callback.onFriendListLoaded(getList);
              }
            });
    return getList;
  }
}
