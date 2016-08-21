package edu.brown.cs.eronning.tvtime;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * ServerData class stores all server related information.
 *
 * @author eronning
 *
 */
public class ServerData {
  private User currUser = null;
  private HashMap<String, User> users;
  private HashMap<String, HashSet<String>> nameToUsers;
  private String page;
  private String friendId;
  private String showId;
  private final String defaultAvatar =
    "https://s3-us-west-2.amazonaws.com/s.cdpn.io/4273/tyrion-lannister.png";

  private String highlightsShowId;
  private int highlightsNumEpsBehind = -1;
  /**
   * ServerData constructor.
   *
   */
  public ServerData() {
    this.friendId = "";
    this.users = new HashMap<String, User>();
    this.nameToUsers = new HashMap<String, HashSet<String>>();
  }

  public User getCurrUser() throws UserNotLoggedInException {
    if (currUser != null) {
      return currUser;
    } else {
      throw new UserNotLoggedInException();
    }
  }
  
  public void setCurrUser(User loggedIn) {
    currUser = loggedIn;
  }
  
  public Collection<User> getAllUsers() {
    return Collections.unmodifiableCollection(users.values());
  }
  
  public void setHighlightsShowId(String id) {
    this.highlightsShowId = id;
  }
  
  public void setHighlightsNumEpsBehind(int n) {
    this.highlightsNumEpsBehind = n;
  }
  
  public String getHighlightsShowId() {
    return highlightsShowId;
  }
  
  public int getHighlightsNumEpsBehind() {
    while (highlightsNumEpsBehind == -1) {
      
    }
    return highlightsNumEpsBehind;
  }
  
  /**
   * getNamedUsers gets the users that start with a name.
   *
   * @param name
   *          is the name to search with
   * @return a optional hashset with all users that have the name
   */
  public Optional<HashSet<String>> getNamedUsers(String name) {
    if (nameToUsers.containsKey(name)) {
      return Optional.of(nameToUsers.get(name));
    }
    return Optional.empty();
  }

  /**
   * setNamedUsers sets the users with a given name.
   *
   * @param name
   *          the name to set
   * @param namedUsers
   *          the hash set of user ids with that name
   */
  public void setNamedUsers(String name, HashSet<String> namedUsers) {
    nameToUsers.put(name, namedUsers);
  }

  /**
   * getDefaultAvatar gets the default avatar image.
   *
   * @return the default avatar
   */
  public String getDefaultAvatar() {
    return defaultAvatar;
  }

  /**
   * getShowId gets the current show id.
   *
   * @return the show id
   */
  public String getShowId() {
    return showId;
  }

  /**
   * setShowId sets the current show id.
   *
   * @param showId
   *          to set the current show id to
   */
  public void setShowId(String showId) {
    this.showId = showId;
  }

  /**
   * getFriendId gets the current friend id
   *
   * @return the friend id
   */
  public String getFriendId() {
    return friendId;
  }

  /**
   * setFriendId sets the current friend id.
   *
   * @param friendId
   *          to set the current friend id to
   */
  public void setFriendId(String friendId) {
    this.friendId = friendId;
  }

  /**
   * getId gets the current user id.
   *
   * @return the id
   */
  public String getUserId() {
    assert(currUser != null);
    return currUser.getId();
  }
  
  /**
   * sets the current user id.
   *
   * @param id
   *          the userId to set id to
   */
  public void setUserId(String id) {
    assert(currUser != null);
    this.currUser.setId(id);
  }

  /**
   * getEncodedUserId gets the current user id in encoded format.
   *
   * @return encoded user id
   */
  public String getEncodedUserId() {
    assert(currUser != null);
    return currUser.getEncodedUserId();
  }

  /**
   * setEncodedUserId sets the current encoded user id.
   *
   * @param encodedUserId
   *          id to be set
   */
  public void setEncodedUserId(String encodedUserId) {
    assert(currUser != null);
    currUser.setEncodedUserId(encodedUserId);
  }
  
  /**
   * getPage gets the current page.
   *
   * @return current page
   */
  public String getPage() {
    assert(page != null);
    return page;
  }

  /**
   * setPage sets the current page.
   *
   * @param page
   *          the page to set the current page to
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * getUser gets a optional user from a user id.
   *
   * @param id
   *          of the user to search for
   * @return the user
   */
  public Optional<User> getUser(String id) {
    if (users.containsKey(id)) {
      return Optional.of(users.get(id));
    }
    return Optional.empty();
  }

  /**
   * storeUserByID stores user object by user id
   *
   * @param id
   *          the user id to set
   * @param user
   *          the new user to set to
   */
  public void storeUserByID(String id, User user) {
    users.put(id, user);
  }

  /**
   * removeUser removes a user.
   *
   * @param id
   *          user id to remove
   */
  public void removeUser(String id) {
    users.remove(id);
    for (Entry<String, HashSet<String>> e : nameToUsers.entrySet()) {
      HashSet<String> namedUsers = e.getValue();
      if (namedUsers.contains(id)) {
        namedUsers.remove(id);
      }
    }
  }

  /**
   * serializeSave serializes all user information.
   *
   */
  public void serializeSave() {
    try {
      FileOutputStream fileOut = new FileOutputStream("tvtimeData");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(users);
      out.writeObject(nameToUsers);
      out.close();
      fileOut.close();
    } catch (IOException e) {
      System.out
        .println("ERROR: there was an error in saving serialized data.");
      e.printStackTrace();
    }
  }

  /**
   * serializeLoad loads all of the users serialized information.
   *
   */
  @SuppressWarnings("unchecked")
  public void serializeLoad() {
    try {
      FileInputStream fileIn = new FileInputStream("tvtimeData");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      users = (HashMap<String, User>) in.readObject();
      nameToUsers = (HashMap<String, HashSet<String>>) in.readObject();
      in.close();
      fileIn.close();
    } catch (IOException i) {
      serializeSave();
      System.out.println("ERROR: needed to serialize data.");
      return;
    } catch (ClassNotFoundException c) {
      System.out.println("HashMap class not found in deserializing.");
      return;
    }
  }

}
