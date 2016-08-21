package edu.brown.cs.eronning.startup;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * SignUpHandler sends to the signup page.
 *
 * @author eronning
 *
 */
public class SignUpHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * SignUpHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public SignUpHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Signup: ");
    serverData.setPage("signup");
    return new ModelAndView(variables, "signup.ftl");
  }
}
