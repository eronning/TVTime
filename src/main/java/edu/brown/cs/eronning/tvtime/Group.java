package edu.brown.cs.eronning.tvtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Show;

/**
 * Defines a group of users watching a show together.
 */
public class Group implements Serializable {

  private static final long serialVersionUID = 1L;
  private User leader; // may not need a group leader
  private List<User> members;
  private Show show;
  private TimeBlock time;

  public Group(User leader, Show show) {
    Preconditions.checkNotNull(leader);
    Preconditions.checkNotNull(show);
    this.leader = leader;
    members = new ArrayList<User>();
    // members.add(leader); the leader is a member?
    this.show = show;
  }

  /**
   * Gets the leader of this group.
   *
   * @return the leader of this group.
   */
  public User getLeader() {
    return leader;
  }

  /**
   * Gets the set of all group members.
   *
   * @return the set of all group members.
   */
  public List<User> getMembers() {
    return members;
  }

  /**
   * Adds a new member to the group.
   *
   * @param u
   *          the User to add.
   */
  public void addUser(User u) {
    Preconditions.checkNotNull(u);
    members.add(u);
  }

  /**
   * Removes a user from the group.
   *
   * @param u
   *          the user to remove.
   * @return true if the user was successfully removed.
   */
  public boolean removeUser(User u) {
    return members.remove(u);
  }

  /**
   * Gets the show that the group is watching.
   *
   * @return the show associated with this group.
   */
  public Show getShow() {
    return show;
  }

  /**
   * Produces a mapping of each user in the group to an integer representing
   * their progress in a show relative to the group leader. e.g. -1 represent
   * a user that is one episode behind the group leader.
   * @return a mapping of user to show progress relative to leader.
   */
  public Map<User, Integer> epProgress() {
    Map<User, Integer> toReturn = new HashMap<User, Integer>();
    Optional<Episode> leaderEp = leader.getCurrEpisode(show);
    int leaderEpNum = 1;
    if (leaderEp.isPresent()) {
      leaderEpNum = leaderEp.get().getEpisodeNum();
      for (User u : members) {
        Optional<Episode> memberEp = u.getCurrEpisode(show);
        if (memberEp.isPresent()) {
          toReturn.put(u, memberEp.get().getEpisodeNum() - leaderEpNum);
        } else {
          toReturn.put(u, 1 - leaderEpNum);
        }
      }
    }
    //System.out.println("Returning " + toReturn);
    return toReturn;
  }

  /**
   * Sets the watching time for this group, and sends an invitation to all group
   * members.
   *
   * @param t
   *          the time block for the show to be watched.
   */
  public void chooseTime(TimeBlock t) {
    time = t;
    //for (User u : members) {
      // WatchRequest req = new WatchRequest(leader, u, this);
      // u.invited(req);
      // leader.addSent(req);
    //}
  }

  /**
   * Gets the time block for this show.
   *
   * @return the time block for the group.
   */
  public TimeBlock getTime() {
    return time;
  }
}
