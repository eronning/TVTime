package edu.brown.cs.eronning.friends;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

public class PopulateFriendsHandler implements Route {
  private ServerData serverData;
  private static final Gson GSON = new Gson();

  public PopulateFriendsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> variables;
    try {
      Set<String> allFriends = serverData.getCurrUser().getFriends();

      JsonArray myFriends = new JsonArray();

      for (String friendID : allFriends) {
        Optional<User> friendOption = serverData.getUser(friendID);
        assert (friendOption.isPresent());
        User friend = friendOption.get();
        JsonObject friendObj = new JsonObject();
        friendObj.addProperty("name", friend.getName());
        friendObj.addProperty("email", friend.getEmail());
        friendObj.addProperty("avatar", friend.getAvatar());
        myFriends.add(friendObj);
      }
      variables = ImmutableMap.of("myFriends", myFriends);

      return GSON.toJson(variables);
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
  }
}