package edu.brown.cs.eronning.account;

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
 * AccountAvatarHandler controls the changing of a avatar.
 *
 * @author eronning
 *
 */
public class AccountAvatarHandler implements Route {

  private static final Gson GSON = new Gson();
  private ServerData serverData;

  /**
   * AccountAvatarHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public AccountAvatarHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    String email = qm.value("email");
    String avatar = qm.value("avatar");
    Optional<User> userOption = serverData.getUser(email);
    if (userOption.isPresent()) {
      User user = userOption.get();
      user.setAvatar(avatar);
      serverData.storeUserByID(email, user);
    } else {
      System.out
        .println("ERROR: there was an error in updating the avatar (1).");
    }
    serverData.serializeSave();
    Map<String, Object> variables =
      ImmutableMap.of("response", "empty response");
    return GSON.toJson(variables);
  }
}