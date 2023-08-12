package edu.northeastern.coinnect.models.userModels.Friends;

import edu.northeastern.coinnect.R;

public enum FriendIcons {

    ASTRONAUT(R.mipmap.ic_friend_astronaut),
    BEAR(R.mipmap.ic_friend_bear),
    CAT(R.mipmap.ic_friend_cat),
    PANDA(R.mipmap.ic_friend_panda),
    RABBIT(R.mipmap.ic_friend_rabbit);

    private int imageResource;

    FriendIcons(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }
}
