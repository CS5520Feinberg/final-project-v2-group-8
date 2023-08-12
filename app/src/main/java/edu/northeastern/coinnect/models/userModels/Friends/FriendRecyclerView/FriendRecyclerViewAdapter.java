package edu.northeastern.coinnect.models.userModels.Friends.FriendRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.coinnect.R;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private Context parentContext;
    private List<FriendModel> friendCardList;

    public FriendRecyclerViewAdapter(List<FriendModel> friendCardList) {
        this.friendCardList = friendCardList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parentContext = parent.getContext();
        View view = LayoutInflater.from(this.parentContext)
                .inflate(R.layout.card_friend, parent, false);

        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendModel friend = friendCardList.get(position);
        String username = friend.getUsername();

        holder.setUsername(username);
    }

    @Override
    public int getItemCount() {
        return this.friendCardList.size();
    }

    public void addCard(FriendModel newFriend) {
        friendCardList.add(newFriend);
        notifyItemInserted(friendCardList.size() - 1);
    }


}
