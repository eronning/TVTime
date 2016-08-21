package edu.brown.cs.eronning.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.ServerData;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateCalHandler implements Route{
  
  private ServerData serverData;
  private static final Gson GSON = new Gson();
  
  public UpdateCalHandler(ServerData serverData){
    this.serverData = serverData;
  }
  
  @Override
  public Object handle(final Request req, final Response res) {
    QueryParamsMap qm = req.queryMap();
    JsonParser parser = new JsonParser();
    JsonArray arr = parser.parse(qm.value("users")).getAsJsonArray();
    
    List<User> userList = new ArrayList<User>();
    for(int i = 0; i< arr.size(); i++){
      String userEmail = arr.get(i).toString().replaceAll("\"", "");
      
      Optional<User> ou = serverData.getUser(userEmail);
      if(ou.isPresent()){
        userList.add(ou.get());
      }else{
        System.err.println("NO User to find");
      }
    }
    
    Map<Integer, List<TimeBlock>> toReturn = Scheduler.getCommonTimeblocks(userList);
    Map<String, Object> response = ImmutableMap.of("commonTime", toReturn);
    return GSON.toJson(response);
  }

}
