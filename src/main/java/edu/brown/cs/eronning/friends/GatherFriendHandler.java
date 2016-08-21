package edu.brown.cs.eronning.friends;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

public class GatherFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * FriendsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public GatherFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(Request req, Response res) {

    boolean areFriends = false;
    boolean requestedFriend = false;
    Map<String, Object> variables =
      ImmutableMap.of("areFriends", areFriends, "friend", "", "commonShows",
        "", "commonFriends", "");
    try {
      String friendEmail = serverData.getFriendId();

      Optional<User> friendOption = serverData.getUser(friendEmail);
      if (friendOption.isPresent()) {
        User friend = friendOption.get();
        JsonObject jsonCurrFriend = new JsonObject();
        jsonCurrFriend.addProperty("name", friend.getName());
        jsonCurrFriend.addProperty("email", friend.getEmail());
        jsonCurrFriend.addProperty("avatar", friend.getAvatar());

        User user = serverData.getCurrUser();
        Collection<Show> friendShows = friend.getShows();
        Collection<Show> userShows = user.getShows();
        JsonArray commonShows = new JsonArray();
        for (Show show : friendShows) {
          if (userShows.contains(show)) {
            JsonObject jsonShow = new JsonObject();
            jsonShow.addProperty("id", show.getId());
            jsonShow.addProperty("name", show.getName());
            jsonShow.addProperty("image", show.getImage());
            jsonShow.addProperty("description", show.getDescription());
            jsonShow.addProperty("runtime", show.getRuntime());
            jsonShow.addProperty("seasons", show.getNumSeasons());
            jsonShow.addProperty("genres", show.getGenres().toString());
            commonShows.add(jsonShow);
          }
        }

        HashSet<String> friendFriends = friend.getFriends();
        HashSet<String> userFriends = user.getFriends();

        if (friendFriends.contains(user.getEmail())
          && userFriends.contains(friendEmail)) {
          areFriends = true;
        } else if (friend.gotFriendReqFrom(user)) {
          requestedFriend = true;
        }

        JsonArray commonFriends = new JsonArray();
        for (String friendString : friendFriends) {
          if (userFriends.contains(friendString)) {
            Optional<User> userFriendOption = serverData.getUser(friendString);
            if (userFriendOption.isPresent()) {
              User userFriend = userFriendOption.get();
              JsonObject jsonFriend = new JsonObject();
              jsonFriend.addProperty("name", userFriend.getName());
              jsonFriend.addProperty("email", userFriend.getEmail());
              jsonFriend.addProperty("avatar", userFriend.getAvatar());
              commonFriends.add(jsonFriend);
            } else {
              System.out.println("ERROR: common friend wasn't found.");
            }
          }
        }

        variables =
          ImmutableMap.<String, Object> builder().put("areFriends", areFriends)
          .put("requestedFriend", requestedFriend)
          .put("friend", jsonCurrFriend).put("commonShows", commonShows)
          .put("commonFriends", commonFriends).build();
      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: User not logged in.");
      variables = ImmutableMap.of("error", "User not logged in.");
    }

    return GSON.toJson(variables);
  }
}
