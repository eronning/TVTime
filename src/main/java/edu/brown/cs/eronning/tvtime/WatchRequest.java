package edu.brown.cs.eronning.tvtime;

import java.time.LocalDateTime;

import com.google.common.base.Preconditions;

import edu.brown.cs.denj.data.Show;

public class WatchRequest extends UserRequest {

  private static final long serialVersionUID = 1L;
  private Group group;

  public WatchRequest(User sender, User recipient, Group group, LocalDateTime now) {
//    Preconditions.checkNotNull(sender);
//    Preconditions.checkNotNull(recipient);
    super(sender, recipient, now);
    Preconditions.checkNotNull(group);

//    this.sender = sender;
//    this.recipient = recipient;
    this.group = group;
    //recipients = new HashMap<User,Boolean>();
    //recipients.put(recipient, false);
    //this.show = s;
    //recipient.invited(this); //sends the invite
  }
  
  public boolean tryAccept(boolean overwrite) {
    boolean canAccept = getRecipient().scheduleShowWithGroup(group, overwrite);
    if (canAccept) { 
      accept();
    }
    return canAccept;
  }

  @Override
  public void accept() {
    super.accept();
  }

  /**
   * Gets the show to be watched.
   * @return the show.
   */
  public Show getShow() {
    return group.getShow();
  }
  
  public Group getGroup() {
    return group;
  }

}
