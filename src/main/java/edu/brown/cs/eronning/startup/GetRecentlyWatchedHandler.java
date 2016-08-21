package edu.brown.cs.eronning.startup;

import java.time.LocalDateTime;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import edu.brown.cs.eronning.tvtime.UserNotLoggedInException;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetRecentlyWatchedHandler implements Route {  
  
  private ServerData serverData;
  private static final Gson GSON = new Gson();
  
  public GetRecentlyWatchedHandler(ServerData serverData) {
    this.serverData = serverData;
  }
  
  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> response;
    
    try {
      User currUser = serverData.getCurrUser();
      LocalDateTime loginTime = LocalDateTime.now();
      response = ImmutableMap.of("recentlyWatched", currUser.sinceLastLogin(loginTime));
    } catch (UserNotLoggedInException e) {
      System.out.println("ERROR: Cannot access page, user not logged in.");
      response = ImmutableMap.of("error", "User not logged in.");
    }
    
    return GSON.toJson(response);
  }
}