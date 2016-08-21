package edu.brown.cs.eronning.startup;

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
 * LoginHandler runs the login form.
 *
 * @author eronning
 *
 */
public class LoginHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * LoginHandler constructor.
   *
   * @param serverData
   *          data from the server
   */
  public LoginHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String email = qm.value("email");
    String password = qm.value("password");
    String response = "email";
    Optional<User> userOption = serverData.getUser(email);
    if (userOption.isPresent()) {
      User user = userOption.get();
      String uPassword = user.getPassword();
      response = "password";
      if (password.equals(uPassword)) {
        response = "true";
        serverData.setCurrUser(user);
        Map<String, Object> variables =
            ImmutableMap.of("response", response, "encoded",
              serverData.getEncodedUserId());
        return GSON.toJson(variables);
      }
    }
    Map<String, Object> variables =
        ImmutableMap.of("response", response);
    return GSON.toJson(variables);
  }
}