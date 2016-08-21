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

public class RejectFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  public RejectFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    boolean friendAdded = false;
    try {
      User currUser = serverData.getCurrUser();
      Optional<User> rejectedFriendOption = serverData.getUser(req.queryParams("deniedFriend"));
      assert(rejectedFriendOption.isPresent());
      User rejectedFriend = rejectedFriendOption.get();
      currUser.resolveFriendReqFrom(rejectedFriend, false);
      friendAdded = false;
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
    Map<String, Object> variables = ImmutableMap.of("friendDenied", friendAdded);
    return GSON.toJson(variables);
  }
}