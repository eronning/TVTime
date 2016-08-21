package edu.brown.cs.eronning.startup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * SignUpResultsHandler controls a post for sign up.
 *
 * @author eronning
 *
 */
public class SignUpResultsHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * SignUpResultsHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public SignUpResultsHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    Map<String, Object> variables;
    QueryParamsMap qm = req.queryMap();
    String name = qm.value("name");
    String email = qm.value("email");
    String password = qm.value("password");
    boolean response;
    Optional<User> userOption = serverData.getUser(email);
    if (userOption.isPresent()) {
      response = false;
      variables = ImmutableMap.of("response", response, "encoded", "");
    } else {
      response = true;
      User user =
        new User(name, email, password, serverData.getDefaultAvatar());
      serverData.setCurrUser(user);
      serverData.storeUserByID(email, user);
      HashSet<String> namedUsers = new HashSet<String>();
      Optional<HashSet<String>> namedUsersOption =
        serverData.getNamedUsers(name);
      if (namedUsersOption.isPresent()) {
        namedUsers = namedUsersOption.get();
      }
      namedUsers.add(email);
      serverData.setNamedUsers(name, namedUsers);
      try {
        serverData.setEncodedUserId(URLEncoder.encode(email, "UTF-8"));
        serverData.serializeSave();
      } catch (UnsupportedEncodingException e) {
        System.out.println("There was an error when encoding the id");
      }
      variables =
        ImmutableMap.of("response", response, "encoded",
          serverData.getEncodedUserId());
    }
    return GSON.toJson(variables);
  }
}