package edu.northeastern.coinnect.activities.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.home.HomeActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.databinding.ActivityFriendsBinding;
import edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView.FriendModel;
import edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView.FriendRecyclerViewAdapter;

public class FriendsActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;
    private List<FriendModel> friendList;
    private RecyclerView friendRecyclerView;


    private void setupRecyclerView(ActivityFriendsBinding binding) {
        this.friendRecyclerView = binding.friendsRecyclerView;
        this.friendRecyclerView.setHasFixedSize(true);
        this.friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.friendRecyclerView.setAdapter(this.friendRecyclerViewAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edu
            .northeastern
            .coinnect
            .databinding
            .ActivityFriendsBinding binding = ActivityFriendsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        this.friendList = new ArrayList<FriendModel>();
        FriendModel friend1 = new FriendModel("Dean");
        FriendModel friend2 = new FriendModel("Prajwal");
        FriendModel friend3 = new FriendModel("Jon");
        FriendModel friend4 = new FriendModel("Omkar");
        FriendModel friend5 = new FriendModel("Peter");
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);
        friendList.add(friend4);
        friendList.add(friend5);


        this.friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(friendList);
        this.setupRecyclerView(binding);

        BottomNavigationView navView = binding.bottomNavFriends;
        navView.setSelectedItemId(R.id.friends);
        menuBarActions(navView);
    }

    protected void menuBarActions(BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item ->  {
            if(item.getItemId() == R.id.homeActivity) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0,0);

            } else if(item.getItemId() == R.id.friends) {
                return true;
            } else if (item.getItemId() == R.id.transactionActivity) {
                startActivity(new Intent(getApplicationContext(), TransactionsActivity.class));
                overridePendingTransition(0,0);
            } else {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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