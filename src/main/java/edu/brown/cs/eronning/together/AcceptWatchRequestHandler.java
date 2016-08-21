package edu.brown.cs.eronning.together;

import java.util.Map;
import java.util.Optional;

import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import edu.brown.cs.eronning.tvtime.WatchRequest;

public class AcceptWatchRequestHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;
  private TVTQuery database;

  public AcceptWatchRequestHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    boolean watchRequestAccepted = false;
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
      // try without overwriting previously scheduled show first
      if (!wReq.tryAccept(false)) {
        if (currUser.alreadyWatchingWithGroup(show)) {
          return GSON.toJson(ImmutableMap.of(
              "warning", "You're watching this show with another group! Delete that block if you'd like to watch with this group instead.",
              "senderId", req.queryParams("senderId"),
              "showId", showId));
        } else {
          assert(currUser.alreadyWatching(show));
          return GSON.toJson(ImmutableMap.of(
              "warning", "You're already watching this show alone! Delete that block if you'd like to watch with the group instead.",
              "senderId", req.queryParams("senderId"),
              "showId", showId));
        }
      } else {
        //try {
//          System.out.println("1" + wReq.getGroup());
//          System.out.println("2" + wReq.getGroup().epProgress());
//          System.out.println(friend);
        int progress = wReq.getGroup().epProgress().get(currUser);
        if (progress < 0) {
          //behind case
          return GSON.toJson(ImmutableMap.of(
              "warning", "Behind",
              "numBehind", progress,
              "senderId", req.queryParams("senderId"),
              "showId", showId));

        } else if (progress > 0) {
          //ahead case
          return GSON.toJson(ImmutableMap.of(
              "warning", "Ahead",
              "numAhead", progress,
              "senderId", req.queryParams("senderId"),
              "showId", showId));

        }
        watchRequestAccepted = true;
        currUser.resolveWatchReq(wReq);
      }
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      return GSON.toJson(ImmutableMap.of("error", "User not logged in."));
    }
    Map<String, Object> variables = ImmutableMap.of("watchRequestAccepted", watchRequestAccepted);
    return GSON.toJson(variables);
  }
}
