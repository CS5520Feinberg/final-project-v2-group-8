package edu.northeastern.coinnect.activities.friends.FriendRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.coinnect.R;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private Context context;

    private List<String> friendCardList;

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater
                        .from(this.context)
                        .inflate(R.layout.card_friend, parent, false);

        return new FriendViewHolder(view);
    }
    public FriendRecyclerViewAdapter(List<String> friendCardList) {
        this.friendCardList = friendCardList;
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
