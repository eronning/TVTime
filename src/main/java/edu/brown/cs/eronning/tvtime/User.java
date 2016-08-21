package edu.brown.cs.eronning.tvtime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.base.Preconditions;
import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Show;

/**
 * User class.
 *
 * @author eronning
 *
 */
public class User implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private String avatar;
  private String name;
  private String email;
  private String password;
  private Map<Show, Episode> showToCurrentEp;
  private boolean[] availableTime; // initial setting by user
  private Map<String, Show> likedShowIDstoShows;
  private Map<Show, TimeBlock> calendar;
  private HashSet<String> friends;
  private LocalDateTime lastLogin;
  private Map<Show, Group> groups; // partays
  private List<UserRequest> invites;
  private List<UserRequest> sent;
  private String id;
  private String encodedUserId;
  private int maxNumRecurs = 1; // for frontend recurring events

  /**
   * User constructor.
   *
   * @param name
   *          the name of the user
   * @param email
   *          the email of the user
   * @param password
   *          the password of the user
   * @param avatar
   *          the users avatar
   */
  public User(String name, String email, String password, String avatar) {
    this.name = name;
    this.password = password;
    this.avatar = avatar;
    availableTime = new boolean[7 * 48];
    calendar = new HashMap<>();
    friends = new HashSet<>();
    likedShowIDstoShows = new HashMap<>();
    showToCurrentEp = new HashMap<>();
    for (Show s : likedShowIDstoShows.values()) {
      showToCurrentEp.put(s, s.getEpInSeason(1, 1));
    }
    lastLogin = LocalDateTime.now();
    invites = new ArrayList<UserRequest>();
    sent = new ArrayList<UserRequest>();
    groups = new HashMap<Show, Group>();
    this.id = email;
    this.email = email;
  }

  public Optional<Episode> getCurrEpisode(Show show) {
    if (showToCurrentEp.containsKey(show)) {
      return Optional.of(showToCurrentEp.get(show));
    }
    return Optional.empty();
  }

  public void updateCurrEpisode(Show show, int epNum) {
    Episode ep = show.getEp(epNum);
    if (showToCurrentEp.containsKey(show)) {
      showToCurrentEp.put(show, ep);
    }
  }

  public Map<String, Integer> getShowRecurrenceNumbers() {
    Map<String, Integer> toReturn = new HashMap<>();
    for (Show s : likedShowIDstoShows.values()) {
      assert (showToCurrentEp.containsKey(s));
      int currentEpNum = showToCurrentEp.get(s).getEpisodeNum();
      toReturn.put(s.getName(), s.getNumEpsides() - currentEpNum);
    }
    return toReturn;
  }

  public int getAvailabilityRecurrence() {
    return maxNumRecurs;
  }

  public void setAllFalse() {
    availableTime = new boolean[7 * 48];
  }

  void setId(String id) {
    this.id = id;
  }

  String getId() {
    assert (this.id != null);
    return this.id;
  }

  void setEncodedUserId(String encodedId) {
    this.encodedUserId = encodedId;
  }

  String getEncodedUserId() {
    return this.encodedUserId;
  }

  /**
   * getShows gets the users shows.
   *
   * @return the users shows
   */
  public Collection<Show> getShows() {
    return likedShowIDstoShows.values();
  }

  public void addShow(String id, int startIndex, int endIndex) {
    id = id.replaceAll("\"", "");
    Show s = likedShowIDstoShows.get(id);
    if (s == null) {
      addShow(s);
    }
    TimeBlock newTB = new TimeBlock(startIndex, endIndex);
    newTB.setID(id);
    calendar.put(s, newTB);
    maxNumRecurs = Math.max(maxNumRecurs, s.getNumEpsides());
  }

  /**
   * Schedules user to watch given show with group
   *
   * @param group
   *          group to watch with
   * @param overwrite
   *          overwrite other watching times for this show
   * @return true if successfully scheduled to watch with group
   */
  public boolean scheduleShowWithGroup(Group group, boolean overwrite) {
    Show showToSchedule = group.getShow();
    addShow(showToSchedule);
    if (!overwrite) {
      if (alreadyWatching(showToSchedule)) {
        return false;
      } else {
        calendar.put(showToSchedule, group.getTime());
        groups.put(showToSchedule, group);
        WatchRequest wReq = getWatchReqFor(showToSchedule, group.getLeader());
        if (wReq != null) {
          resolveWatchReq(wReq);
        }
        return true;
      }
    } else {
      removeScheduledShow(showToSchedule);
      calendar.put(showToSchedule, group.getTime());
      groups.put(showToSchedule, group);
      WatchRequest wReq = getWatchReqFor(showToSchedule, group.getLeader());
      if (wReq != null) {
        resolveWatchReq(wReq);
      }
      return true;
    }
  }

  public boolean alreadyWatching(Show show) {
    return calendar.containsKey(show);
  }

  public boolean alreadyWatchingWithGroup(Show show) {
    return alreadyWatching(show) && groups.containsKey(show);
  }

  public void clearSchedule() {
    calendar = new HashMap<>();
  }

  /**
   * addShow add a show to the users list.
   *
   * @param s
   *          show to add
   * @return true if user didn't already have this show liked
   */
  public boolean addShow(Show s) {
    if (likedShowIDstoShows.containsKey(s.getId())) {
      return false;
    } else {
      likedShowIDstoShows.put(s.getId(), s);
      showToCurrentEp.put(s, s.getEp(1)); // progress set to ep1 by default
      return true;
    }
  }

  /**
   * Removes individual/group show watching session
   *
   * @param s
   *          show to remove
   * @return true if user originally watched the show
   */
  public boolean removeScheduledShow(Show s) {
    boolean showScheduled = calendar.containsKey(s);
    if (showScheduled) {
      calendar.remove(s);
    }

    if (groups.containsKey(s)) {
      groups.remove(s);
    }
    return showScheduled;
  }

  public boolean removeShow(Show s) {
    if (likedShowIDstoShows.containsKey(s.getId())) {
      likedShowIDstoShows.remove(s.getId(), s);
      return true;
    } else {
      return false;
    }
  }

  /**
   * getName gets the users name.
   *
   * @return the users name
   */
  public String getName() {
    return name;
  }

  /**
   * setName sets the users name.
   *
   * @param newName
   *          the name to set
   */
  public void setName(String newName) {
    name = newName;
  }

  /**
   * getEmail gets the email of the user.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * setEmail sets the email of the user.
   *
   * @param newEmail
   *          the email to set to
   */
  public void setEmail(String newEmail) {
    email = newEmail;
  }

  /**
   * getPassword gets the users password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * setPassword sets the users password.
   *
   * @param newPassword
   *          the password to set to
   */
  public void setPassword(String newPassword) {
    password = newPassword;
  }

  /**
   * getAvatar gets the users avatar.
   *
   * @return the avatar
   */
  public String getAvatar() {
    return avatar;
  }

  /**
   * setAvatar sets the users avatar
   *
   * @param newAvatar
   *          the avatar to set to
   */
  public void setAvatar(String newAvatar) {
    avatar = newAvatar;
  }

  public void setAvailableTime(int dayOfWeek, int hourNum, boolean bool) {
    int index = getIndex(dayOfWeek, hourNum);
    availableTime[index] = bool;
  }

  public void setAvailableTime(int index, boolean bool) {
    availableTime[index] = bool;
  }

  /**
   * This is the getIndex method.
   *
   * @param dayOfWeek
   *          - ranges from 0 to 6 (Sunday to Saturday)
   * @param hourNum
   *          - ranges from 0 to 47 (12:00 am to 11:30 pm blocks)
   * @return the index corresponding to the dayOfWeek and hourNum
   */
  public int getIndex(int dayOfWeek, int hourNum) {
    return dayOfWeek * 48 + hourNum;
  }

  /**
   * getAvailability method.
   *
   * @param dayOfWeek
   *          - 0 to 6
   * @param hourNum
   *          - 0 to 47
   * @return whether that half hour block is available.
   */
  public boolean getAvailability(int dayOfWeek, int hourNum) {
    return availableTime[getIndex(dayOfWeek, hourNum)];
  }

  /**
   * setAvailableTime Method.
   *
   * @param booArray
   *          - boolean array to be set as availTime.
   */
  public void setAvailableTime(boolean[] booArray) {
    availableTime = booArray;
  }

  public void setShows(Set<Show> shows) {
    for (Show s : shows) {
      likedShowIDstoShows.put(s.getId(), s);
    }
  }

  public boolean[] getAvailableTime() {
    return availableTime;
  }

  /**
   * getFriends gets the users friends.
   *
   * @return the friends
   */
  public HashSet<String> getFriends() {
    return friends;
  }

  /**
   * setFriends sets the users friends.
   *
   * @param newFriends
   *          friends to set to
   */
  public void setFriends(HashSet<String> newFriends) {
    friends = newFriends;
  }

  /**
   * removeFriend removes a friend
   *
   * @param friend
   *          friend to remove
   * @return a boolean on whether or not the user already has this friend
   */
  public boolean removeFriend(User friend) {
    if (friends.contains(friend.getEmail())) {
      friends.remove(friend.getEmail());
      return true;
    }
    return false;
  }

  /**
   * addFriend adds a friend
   *
   * @param friend
   *          friend to add
   * @return a boolean on whether or not the user already has this friend
   */
  public boolean addFriend(User friend) {
    if (friends.contains(friend.getEmail())) {
      return false;
    }
    friends.add(friend.getEmail());
    return true;
  }

  public void setSchedule(Map<Show, TimeBlock> schedule) {
    calendar = schedule;
  }

  public Map<Show, TimeBlock> getSchedule() {
    for (Show s : calendar.keySet()) {
      Preconditions.checkArgument(showToCurrentEp.containsKey(s));
      Episode currEp = showToCurrentEp.getOrDefault(s, s.getEp(1));
      int numAired = s.getMostRecent().getEpisodeNum();

      // The number of weeks ahead to put time block
      calendar.get(s).setRecurrences(numAired - currEp.getEpisodeNum());

    }
    return calendar;
  }

  /**
   * Gets the list of upcoming episodes from the shows that the user is watching
   * in order of watching time.
   *
   * @return the list of episodes that the user will watch over the next week.
   */
  public List<Event> upcomingShows() {
    PriorityQueue<Show> queue =
      new PriorityQueue<Show>(new UpcomingComparator());
    assert (calendar.keySet().size() >= 0);
    for (Show s : calendar.keySet()) {
      queue.add(s);
    }
    List<Event> toReturn = new ArrayList<Event>();
    while (!queue.isEmpty()) {
      Show nextShow = queue.remove();
      assert (showToCurrentEp.containsKey(nextShow));
      LocalDateTime time =
        calendar.get(nextShow).nextStart(LocalDateTime.now());
      Episode ep = showToCurrentEp.get(nextShow);
      Event toAdd = new Event(ep, time);
      toReturn.add(toAdd);
    }
    return toReturn;
  }

  /**
   * Produces the set of all shows that the user should have watched an episode
   * of since last login.
   *
   * @param login
   *          the new login time of the user.
   * @return all shows that the user might have watched. TODO this doesn't
   *         account for multiple episodes of a show per week, since we only
   *         have one time-block per show... Also I have no idea how far ahead
   *         we're scheduling at this point...
   */
  public List<Episode> sinceLastLogin(LocalDateTime login) {
    List<Episode> toReturn = new ArrayList<Episode>();
    for (Show s : calendar.keySet()) {
      // LocalDateTime endTime = calendar.get(s).nextEnd(lastLogin);
      if (calendar.get(s).nextEnd(lastLogin).isBefore(login)) {
        toReturn.add(showToCurrentEp.get(s));
      }
    }
    lastLogin = login;
    // used these to test display for recentlyWatched-related things
    // Show dummyShow =
    // new Show("Show Name", 4, false, "showImageStr", "showID", "showPg", 60,
    // Arrays.asList("Drama", "Comedy"));
    // Episode dummyEp =
    // new Episode(dummyShow, 12, 2, "Ep title", "imgURL", "infoURL",
    // "airDate");
    // toReturn.add(dummyEp);
    // toReturn.add(dummyEp);
    return toReturn;
  }

  /**
   * Signals that an episode of the show has been watched. Increments the user's
   * progress to the next episode of the show if available.
   *
   * @param s
   *          the show that has been watched.
   */
  public void watchedEp(Show s) {
    Episode curr = showToCurrentEp.getOrDefault(s, s.getEp(1));
    Episode nextEp = curr.nextEp(s);
    showToCurrentEp.put(s, nextEp); // TODO account for reaching the end
  }

  /**
   * Sends a new request to the given User to watch a show.
   *
   * @param friend
   *          the friend to invite
   * @param s
   *          the Show to be watched
   */
  public void watchInvite(User friend, Show s) {
    Group currGroup = groups.getOrDefault(s, new Group(this, s));
    UserRequest newRequest =
      new WatchRequest(this, friend, currGroup, LocalDateTime.now());
    sent.add(newRequest);
    friend.invited(newRequest);
  }

  /**
   * Adds the request to this User's list of received requests.
   *
   * @param r
   *          the request.
   */
  public void invited(UserRequest r) {
    invites.add(r);
  }

  /**
   * Sends the given user a friend request from this user.
   *
   * @param me
   *          user sending request
   * @param friend
   *          the friend to send the request to.
   */
  public void friendRequest(User me, User friend) {
    FriendRequest req = new FriendRequest(me, friend, LocalDateTime.now());
    sent.add(req);
    friend.invited(req);
  }

  public boolean gotFriendReqFrom(User user) {
    for (UserRequest r : invites) {
      if (r.getSender().equals(user)) {
        return true;
      }
    }
    return false;
  }

  public boolean resolveFriendReqFrom(User friend, boolean accept) {
    for (UserRequest req : invites) {
      if ((req instanceof FriendRequest)
        && (req.getSender().getId().equals(friend.getId()))) {
        if (accept) {
          req.getSender().addFriend(req.getRecipient());
          req.getRecipient().addFriend(req.getSender());
          req.accept();
        } else {
          req.reject();
        }
        invites.remove(req);
        return true;
      }
    }
    return false;
  }

  public WatchRequest getWatchReqFor(Show show, User friend) {
    for (UserRequest req : invites) {
      if ((req instanceof WatchRequest)
        && (req.getSender().getId().equals(friend.getId()))) {
        WatchRequest wReq = (WatchRequest) req;
        if (wReq.getShow().equals(show)) {
          return wReq;
        }
      }
    }
    return null;
  }

  public boolean resolveWatchReq(WatchRequest req) {
    return invites.remove(req);
  }

  /**
   * Gets a user's group for a show if it exists.
   *
   * @param s
   *          the show to get the group for.
   * @return the show watching group that the user is a part of.
   */
  public Group getGroup(Show s) {
    return groups.get(s);
  }

  /**
   * Gets the list of this user's current notifications to display.
   *
   * @return the list of friend and watching requests and notifications about
   *         accepted friend and watching requests by other users.
   */
  public List<UserRequest> getNotifications() {
    System.out.println("Num invites = " + invites.size());
    System.out.println("Num sent = " + sent.size());
    List<UserRequest> requestsToShow = new ArrayList<>();
    List<UserRequest> copyInvites = new ArrayList<>();
    for (UserRequest r : invites) {
      copyInvites.add(r);
    }

    List<UserRequest> actionizedSent = new ArrayList<>();
    for (int i = 0; i < sent.size(); i++) {
      UserRequest r = sent.get(i);
      if (r.isAccepted()) {
        System.out.println("accepted!");
        actionizedSent.add(r);
        sent.remove(sent.get(i));
        continue;
      } else if (r.isRejected()) {
        System.out.println("rejected..!");
        // we won't tell user if they're rejected
        sent.remove(sent.get(i));
        continue;
      } else {
        System.out.println("nothing");
      }
    }

    while (!copyInvites.isEmpty() && !actionizedSent.isEmpty()) {
      UserRequest reqHead = copyInvites.get(0);
      UserRequest sentHead = actionizedSent.get(0);
      if (reqHead.compareTo(sentHead) <= 0) {
        requestsToShow.add(reqHead);
        copyInvites.remove(0);
      } else {
        requestsToShow.add(sentHead);
        actionizedSent.remove(0);
      }
    }

    if (copyInvites.isEmpty()) {
      requestsToShow.addAll(actionizedSent);
    } else {
      assert (actionizedSent.isEmpty());
      requestsToShow.addAll(copyInvites);
    }

    return requestsToShow;
  }

  /**
   * Sets this user's watching group for a show to the given group.
   *
   * @param g
   *          the group to set.
   */
  public void addGroup(Group g) {
    groups.put(g.getShow(), g); // Note: only 1 group per show

  }

  /**
   * Removes the user from a show group.
   *
   * @param s
   *          the show for the group.
   * @return true if successful.
   */
  public boolean leaveGroup(Show s) {
    Group g = groups.remove(s);
    // TODO remove appropriate block
    return (g != null && g.removeUser(this));
  }

  /**
   * Removes a request from invite list.
   *
   * @param r
   *          the request to remove
   * @return true if successful
   */
  public boolean ignoreRequest(UserRequest r) {
    r.reject();
    return invites.remove(r);
  }

  @Override
  public String toString() {
    return "name: " + name + " email: " + email + " password: " + password;
  }

  private class UpcomingComparator implements Comparator<Show> {

    @Override
    public int compare(Show o1, Show o2) {
      return calendar.get(o1).nextStart(LocalDateTime.now())
        .compareTo(calendar.get(o2).nextStart(LocalDateTime.now()));
      // comparing the start times of the next occurrence of the two blocks
    }

  }
}
