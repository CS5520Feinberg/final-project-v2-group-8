package edu.northeastern.coinnect.activities.friends.FriendRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.models.userModels.Friends.FriendIcons;

public class FriendViewHolder extends RecyclerView.ViewHolder {

    private final TextView username;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        this.username = itemView.findViewById(R.id.tv_friend_name);
        ImageView avatar = itemView.findViewById(R.id.iv_friend_avatar);

        int randomIndex = new Random().nextInt(FriendIcons.values().length);
        int randomAvatar = FriendIcons.values()[randomIndex].getImageResource();

        avatar.setImageResource(randomAvatar);
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        this.username.setText(newUsername);
    }

}
