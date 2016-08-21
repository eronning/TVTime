package edu.brown.cs.eronning.friends;

import java.util.HashSet;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * SearchFriendsHandler controls searching friends.
 *
 * @author eronning
 *
 */
public class SearchFriendsHandler implements Route {

  private ServerData serverData;
  private TVTQuery database;

  /**
   * SearchFriendsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   * @param database
   *          is the show database
   */
  public SearchFriendsHandler(ServerData serverData, TVTQuery database) {
    this.serverData = serverData;
    this.database = database;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String input = qm.value("input");
    JsonArray response = new JsonArray();
    JsonObject responseJson;
    Optional<User> userOption = serverData.getUser(input);
    Optional<HashSet<String>> namedUsersOption =
      serverData.getNamedUsers(input);
    if (userOption.isPresent()) {
      User user = userOption.get();
      responseJson = new JsonObject();
      responseJson.addProperty("name", user.getName());
      responseJson.addProperty("email", user.getEmail());
      responseJson.addProperty("avatar", user.getAvatar());
      response.add(responseJson);
    } else if (namedUsersOption.isPresent()) {
      HashSet<String> namedUsers = namedUsersOption.get();
      for (String email : namedUsers) {
        responseJson = new JsonObject();
        Optional<User> namedUserOption = serverData.getUser(email);
        if (namedUserOption.isPresent()) {
          User user = namedUserOption.get();
          responseJson.addProperty("name", user.getName());
          responseJson.addProperty("email", user.getEmail());
          responseJson.addProperty("avatar", user.getAvatar());
        } else {
          System.out
          .println("ERROR: the namedUser option wasn't present in search friends");
        }
        response.add(responseJson);
      }
    }
    return response.toString();
  }
}
