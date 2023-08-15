package edu.northeastern.coinnect.activities.friends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FriendsActivity extends AppCompatActivity {

    private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();

    private RecyclerView friendRecyclerView;
    private int REQUEST_ADD_FRIEND = 1;
    private final UsersRepository usersRepository = UsersRepository.getInstance();

    private edu.northeastern.coinnect.databinding.ActivityFriendsBinding binding;
    private TextView emptyStateView;
    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersRepository.setCurrentUserName(firebaseDBHandler.getCurrentUserName());
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        FloatingActionButton addFriendFAB = binding.floatingActionButtonAddFriend;
        emptyStateView = binding.emptyStateViewFriends;

        addFriendFAB.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddFriend.class);
            startActivityForResult(intent, REQUEST_ADD_FRIEND);
        });
        View view = binding.getRoot();
        setContentView(view);
        setupRecyclerView();

        BottomNavigationView navView = binding.bottomNavFriends;
        navView.setSelectedItemId(R.id.friendsActivity);
        this.setupNavBarActions(navView);
    }

    private void setupRecyclerView() {
        // getting this off the Main thread, was causing crash.
        Executor executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<String> friendList = new ArrayList<>();
            firebaseDBHandler.getCurrentUserFriends()
                    .addOnSuccessListener(dataSnapshot -> {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            friendList.add(Objects.requireNonNull(child.getValue()).toString());
                        }
                        usersRepository.setCurrentUserFriendsList(friendList);

                        mainThreadHandler.post(() -> {
                            if (friendList == null || friendList.isEmpty()) {
                                emptyStateView.setVisibility(View.VISIBLE);
                            } else {
                                friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(usersRepository.getCurrentUserFriends());
                                friendRecyclerView = binding.friendsRecyclerView;
                                friendRecyclerView.setHasFixedSize(true);
                                friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                friendRecyclerView.setAdapter(friendRecyclerViewAdapter);
                                friendRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    });
        });
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

        if (friendRecyclerView != null && this.friendRecyclerView.getLayoutManager() != null) {
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

        if (requestCode == REQUEST_ADD_FRIEND && resultCode == Activity.RESULT_OK) {
            boolean requestComplete = data.getBooleanExtra("requestComplete", false);
            if (requestComplete) {
                friendRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
