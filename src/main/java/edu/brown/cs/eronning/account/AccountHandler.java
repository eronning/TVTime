package edu.brown.cs.eronning.account;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.eronning.tvtime.ServerData;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * AccountHandler handles the account page.
 *
 * @author eronning
 *
 */
public class AccountHandler implements TemplateViewRoute {

  private ServerData serverData;

  /**
   * AccountHandler constructor.
   *
   * @param serverData
   *          is the data from the server
   */
  public AccountHandler(ServerData serverData) {
    this.serverData = serverData;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    Map<String, Object> variables = ImmutableMap.of("title", "Account: ");
//    try {
//      serverData.setUserId(URLDecoder.decode(req.params(":id"), "UTF-8"));
//    } catch (UnsupportedEncodingException e) {
//      System.out.println("problem decoding id");
//    }
    serverData.setPage("account");
    return new ModelAndView(variables, "account.ftl");
  }
}