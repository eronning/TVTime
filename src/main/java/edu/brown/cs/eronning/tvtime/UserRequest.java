package edu.brown.cs.eronning.tvtime;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.common.base.Preconditions;

/**
 * Represents invitations between users to watch TV.
 * @author nhyde
 *
 */
public abstract class UserRequest implements Serializable, Comparable<UserRequest> {

  private static final long serialVersionUID = 1L;

  private User sender;
  //private Map<User, Boolean> recipients;
  private User recipient;
  //private Group group;
  private boolean accepted;
  private boolean rejected;
  private LocalDateTime reqTime;

  public UserRequest(User sender, User recipient, LocalDateTime now) {
    Preconditions.checkNotNull(sender);
    Preconditions.checkNotNull(recipient);
    //Preconditions.checkNotNull(group);
    this.sender = sender;
    this.recipient = recipient;
    //this.group = group;
    //recipients = new HashMap<User,Boolean>();
    //recipients.put(recipient, false);
    //this.show = s;
    this.accepted = false;
    this.rejected = false;
    this.reqTime = now;
    //recipient.invited(this); //sends the invite
  }

  /**
   * Invites another user to the viewing.
   * @param u the user to add.
   */
//  public void addRecipient(User u) {
//    Preconditions.checkNotNull(u);
//    recipients.put(u, false);
//  }

  /**
   * Produces boolean of whether the recipient has accepted the request.
   * @return true if the given user has accepted this request, false otherwise.
   */
  public boolean isAccepted() {
    return accepted;
  }

  /**
   * Indicates that the user has accepted this request.
   */
  public void accept() {
    accepted = true;
//    group.addUser(recipient);
//    recipient.setGroup(group);
  }

  public boolean isRejected() {
    return rejected;
  }

  public void reject() {
    rejected = true;
  }
  
//  /**
//   * Indicates that the given user has ignored (read: rejected) the request.
//   * Removes the user from the recipients set.
//   * @param u the user ignoring.
//   */
//  public void ignore(User u) {
//    recipients.remove(u);
//  }

  /**
   * Gets the sender of this request.
   * @return the sender.
   */
  public User getSender() {
    return sender;
  }

  /**
   * Get's the recipient of this request.
   * @return the recipient.
   */
  public User getRecipient() {
    return recipient;
  }

  public LocalDateTime getReqTime() {
    return reqTime;
  }
  
//  /**
//   * Gets the show to be watched.
//   * @return the show.
//   */
//  public Show getShow() {
//    return group.getShow();
//  }
  
  @Override
  public int compareTo(UserRequest that) {
    return this.reqTime.compareTo(that.getReqTime());
  }

}
