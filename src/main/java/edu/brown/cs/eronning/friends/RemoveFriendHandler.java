package edu.brown.cs.eronning.friends;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * RemoveFriendHandler controls the adding of a friend.
 *
 * @author eronning
 *
 */
public class RemoveFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * AddFriendHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public RemoveFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    String userEmail = serverData.getUserId();
    String friendEmail = serverData.getFriendId();
    boolean friendRemoved = false;
    Optional<User> userOption = serverData.getUser(serverData.getUserId());
    Optional<User> friendOption = serverData.getUser(serverData.getFriendId());
    if (userOption.isPresent() && friendOption.isPresent()) {
      User user = userOption.get();
      User friend = friendOption.get();
      friendRemoved = user.removeFriend(friend);
//      System.out.println("friend was removed: " + friendRemoved);
//      HashSet<String> friends = user.getFriends();
//      for (String str : friends) {
//        System.out.println("friend: " + str);
//      }
      serverData.storeUserByID(userEmail, user);
      serverData.storeUserByID(friendEmail, friend);
    } else {
      System.out.println("ERROR: not both users exist (friends 1)");
    }
    Map<String, Object> variables =
      ImmutableMap.of("friendAdded", friendRemoved);
    return GSON.toJson(variables);
  }
}