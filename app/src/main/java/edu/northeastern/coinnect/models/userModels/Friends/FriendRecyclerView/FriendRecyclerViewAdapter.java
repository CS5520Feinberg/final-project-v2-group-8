package edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.coinnect.R;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private final ArrayList<String> friendCardList;

    public FriendRecyclerViewAdapter(ArrayList<String> friendCardList) {
        this.friendCardList = friendCardList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context parentContext = parent.getContext();
        View view = LayoutInflater.from(parentContext)
                .inflate(R.layout.card_friend, parent, false);

        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String friend = friendCardList.get(position);
        holder.setUsername(friend);
    }

    @Override
    public int getItemCount() {
        return this.friendCardList.size();
    }

    public void addCard(String newFriend) {
        friendCardList.add(newFriend);
        notifyItemInserted(friendCardList.size() - 1);
    }


}
