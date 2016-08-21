package edu.brown.cs.eronning.friends;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

public class RequestFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  
  public RequestFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables;
    try {
      String friendEmail = req.queryParams("friendEmail");
      boolean friendRequested = false;
      Optional<User> friendOption = serverData.getUser(friendEmail);
      if (friendOption.isPresent()) {
        User user = serverData.getCurrUser();
        User friend = friendOption.get();
        user.friendRequest(user, friend);
        serverData.storeUserByID(user.getEmail(), user); // why is this here -Jaclyn
        serverData.storeUserByID(friendEmail, friend);
        variables = ImmutableMap.of("friendRequested", friendRequested);
      } else {
        System.out.println("ERROR: Friend does not exist.");
        variables = ImmutableMap.of("error", "Friend does not exist.");
      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    }

    return GSON.toJson(variables);
  }
}
