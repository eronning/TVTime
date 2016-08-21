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

/**
 * AddFriendHandler controls the adding of a friend.
 *
 * @author eronning
 *
 */
public class AddFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * AddFriendHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public AddFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    boolean friendAdded = false;
    try {
      User currUser = serverData.getCurrUser();
      Optional<User> friendOption =
        serverData.getUser(req.queryParams("acceptedFriend"));
      assert (friendOption.isPresent());
      User friend = friendOption.get();
      currUser.resolveFriendReqFrom(friend, true);
      friendAdded = true;
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
    Map<String, Object> variables = ImmutableMap.of("friendAdded", friendAdded);
    return GSON.toJson(variables);
  }
}