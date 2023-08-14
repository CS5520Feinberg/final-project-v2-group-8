package edu.northeastern.coinnect.activities.friends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import androidx.annotation.NonNull;
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
import java.util.List;
import java.util.Objects;

public class FriendsActivity extends AppCompatActivity {

  private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();

  private RecyclerView friendRecyclerView;
  private final UsersRepository usersRepository = UsersRepository.getInstance();

  private edu.northeastern.coinnect.databinding.ActivityFriendsBinding binding;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String currentUser = this.usersRepository.getCurrentUserName();
    firebaseDBHandler.setCurrentUserName(currentUser);
    binding = ActivityFriendsBinding.inflate(getLayoutInflater());
    FloatingActionButton addFriendFAB = binding.floatingActionButtonAddFriend;

    addFriendFAB.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), AddFriend.class);
      startActivity(intent);
    });
    View view = binding.getRoot();
    setContentView(view);
    setupRecyclerView();

    BottomNavigationView navView = binding.bottomNavFriends;
    navView.setSelectedItemId(R.id.friendsActivity);
    this.setupNavBarActions(navView);
  }

  private void setupRecyclerView() {

    List<String> friendList = new ArrayList<>();

    firebaseDBHandler.getCurrentUserFriends()
            .addOnSuccessListener(dataSnapshot -> {
              for (DataSnapshot child : dataSnapshot.getChildren()) {
                friendList.add(Objects.requireNonNull(child.getValue()).toString());
              }
              usersRepository.setCurrentUserFriendsList(friendList);
            });


    FriendRecyclerViewAdapter friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(usersRepository.getCurrentUserFriends()); // TODO
    friendRecyclerView = binding.friendsRecyclerView;
    friendRecyclerView.setHasFixedSize(true);
    friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    friendRecyclerView.setAdapter(friendRecyclerViewAdapter);
  }


  protected void setupNavBarActions(BottomNavigationView navView) {
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
}
