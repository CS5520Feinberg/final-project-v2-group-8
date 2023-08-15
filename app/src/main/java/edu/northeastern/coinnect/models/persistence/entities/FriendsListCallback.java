package edu.northeastern.coinnect.models.persistence.entities;

import java.util.List;

public interface FriendsListCallback {
  void onCallback(List<String> friendsList);
}
