package edu.brown.cs.eronning.friends;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class NavFriendHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * AddFriendHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public NavFriendHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String friendEmail = qm.value("friendEmail").replaceAll("[()]", "");
    serverData.setFriendId(friendEmail);
    String idToReturn = "";
    try {
      idToReturn = URLEncoder.encode(friendEmail, "UTF-8");
      serverData.serializeSave();
    } catch (UnsupportedEncodingException e) {
      System.out.println("There was an error when encoding the id");
    }
    Map<String, Object> variables =
      ImmutableMap.of("userId", serverData.getEncodedUserId(), "friendId",
        idToReturn);
    return GSON.toJson(variables);
  }
}