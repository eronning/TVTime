package edu.brown.cs.eronning.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AccountSettingsHandler handles the changing of account settings.
 *
 * @author eronning
 *
 */
public class AccountSettingsHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * AccountSettingsHandler construtor.
   *
   * @param serverData
   *          is the data from the server
   */
  public AccountSettingsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String oldName = qm.value("oldName");
    String newName = qm.value("newName");
    String oldEmail = qm.value("oldEmail");
    String newEmail = qm.value("newEmail");
    String newPassword = qm.value("newPassword");
    boolean response = true;
    boolean sameEmail = oldEmail.equals(newEmail);
    HashSet<String> namedUsers;
    if (sameEmail) {
      Optional<User> userOption = serverData.getUser(oldEmail);
      if (userOption.isPresent()) {
        User user = userOption.get();
        user.setName(newName);
        user.setPassword(newPassword);
        serverData.storeUserByID(oldEmail, user);
        namedUsers = new HashSet<String>();
        updateNamedUser(oldName, newName, oldEmail, newEmail, namedUsers);
      } else {
        System.out
          .println("ERROR: userOption not present in accountSettings(1)");
      }
    } else {
      Optional<User> userOption = serverData.getUser(newEmail);
      if (userOption.isPresent()) {
        response = false;
      } else {
        userOption = serverData.getUser(oldEmail);
        if (userOption.isPresent()) {
          User user = userOption.get();
          user.setName(newName);
          user.setEmail(newEmail);
          user.setPassword(newPassword);
          serverData.storeUserByID(newEmail, user);
          serverData.removeUser(oldEmail);
          updateUserFriends(user, oldEmail, newEmail);
          namedUsers = new HashSet<String>();
          updateNamedUser(oldName, newName, oldEmail, newEmail, namedUsers);
        } else {
          System.out
            .println("ERROR: userOption not present in account settings(2)");
        }
      }
    }
    if (response == true) {
      try {
        serverData.setEncodedUserId(URLEncoder.encode(newEmail, "UTF-8"));
        serverData.serializeSave();
      } catch (UnsupportedEncodingException e) {
        System.out.println("There was an error when encoding the id");
      }
    }
    Map<String, Object> variables = ImmutableMap.of("response", response);
    return GSON.toJson(variables);
  }

  /**
   * updateNamedUser updates the name to user cache.
   *
   * @param oldName
   *          the old name of the user
   * @param newName
   *          the new name of the user
   * @param oldEmail
   *          the old email of the user
   * @param newEmail
   *          the new email of the user
   * @param namedUsers
   *          the named users for the name
   */
  private void updateNamedUser(String oldName, String newName, String oldEmail,
    String newEmail, HashSet<String> namedUsers) {
    Optional<HashSet<String>> namedUsersOption =
      serverData.getNamedUsers(oldName);
    if (namedUsersOption.isPresent()) {
      namedUsers = namedUsersOption.get();
      namedUsers.remove(oldEmail);
    } else {
      System.out
        .println("ERROR: there was an error in updating the named users");
    }
    namedUsers.add(newEmail);
    serverData.setNamedUsers(newName, namedUsers);
  }

  /**
   * updateUserFriends updates the emails stored in the users friends.
   *
   * @param user
   *          the user being updated
   * @param oldEmail
   *          the old email for the user
   * @param newEmail
   *          the new email for the user
   */
  private void updateUserFriends(User user, String oldEmail, String newEmail) {
    HashSet<String> friends = user.getFriends();
    for (String friendString : friends) {
      Optional<User> userOption = serverData.getUser(friendString);
      if (userOption.isPresent()) {
        User friend = userOption.get();
        HashSet<String> friendFriends = friend.getFriends();
        if (friendFriends.contains(oldEmail)) {
          friendFriends.remove(oldEmail);
          friendFriends.add(newEmail);
          friend.setFriends(friendFriends);
          serverData.storeUserByID(friendString, friend);
        } else {
          System.out
            .println("ERROR: there was an error in updating a users friends");
        }
      }
    }
  }
}