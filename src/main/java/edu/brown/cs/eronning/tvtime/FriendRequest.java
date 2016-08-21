package edu.brown.cs.eronning.tvtime;

import java.time.LocalDateTime;

/**
 * Represents a friend request between users.
 */
public class FriendRequest extends UserRequest {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public FriendRequest(User sender, User recipient, LocalDateTime now) {
    super(sender, recipient, now);
  }

  @Override
  public void accept() {
    super.accept();
    getRecipient().addFriend(getSender());
    getSender().addFriend(getRecipient()); //add friends if accepted.
  }

}
