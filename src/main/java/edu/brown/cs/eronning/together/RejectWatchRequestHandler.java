package edu.brown.cs.eronning.together;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import edu.brown.cs.eronning.tvtime.WatchRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class RejectWatchRequestHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  public RejectWatchRequestHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    try {
      User currUser = serverData.getCurrUser();
      Optional<User> friendOption = serverData.getUser(req.queryParams("senderId"));
      assert(friendOption.isPresent());
      User friend = friendOption.get();
      
      String showId = req.queryParams("showId");
      assert(showId != null);
      assert(!showId.equals(""));
      Show show = database.getShow(showId);
      assert(show != null);
      
      WatchRequest wReq = currUser.getWatchReqFor(show, friend);
      currUser.resolveWatchReq(wReq);
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
    Map<String, Object> variables = ImmutableMap.of("watchRequestAccepted", false);
    return GSON.toJson(variables);
  }
}
