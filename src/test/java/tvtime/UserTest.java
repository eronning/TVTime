package tvtime;

import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.brown.cs.denj.data.Episode;
import edu.brown.cs.denj.data.Show;
import edu.brown.cs.denj.data.TVDatabase;
import edu.brown.cs.denj.data.TVTQuery;
import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;


public class UserTest {

   @Test
   public void sinceLastLoginTest() {
   TVTQuery db = new TVDatabase();
   User u = new User("Tyrion", "tyrion_lannister@casterlyrock.gov",
   "12345", "Tyrion");
   u.setAvailableTime(1, 1, true);
   u.setAvailableTime(1, 2, true);
   u.setAvailableTime(1, 3, true);
   u.setAvailableTime(1, 4, true);
   u.setAvailableTime(1, 5, true);
   u.setAvailableTime(4, 6, true);
   u.setAvailableTime(4, 7, true);
   u.addShow(db.getShow("24493"));
   u.addShow(db.getShow("2680"));
   u.addShow(db.getShow("28776"));
   //Scheduler scheduler = new Scheduler();
   Map<Show, TimeBlock> mySchedule = Scheduler.schedule(u);
   // System.out.println(mySchedule.size());
   for(Show show : mySchedule.keySet()) {
   TimeBlock block = mySchedule.get(show);
   // System.out.println(show.getName() + " : " + block);
   }
   LocalDateTime newLogin = LocalDateTime.now().with(
   TemporalAdjusters.next(DayOfWeek.TUESDAY));
   // System.out.println("Next " + newLogin);
   List<Episode> shouldHaveWatched =
   u.sinceLastLogin(LocalDateTime.now().with(
   TemporalAdjusters.next(DayOfWeek.TUESDAY)));
   assertTrue(shouldHaveWatched.size() == 2);
   assertTrue(shouldHaveWatched.get(0).getTitle().equals("Winter is Coming"));
   // System.out.println(u.sinceLastLogin(LocalDateTime.now().with(
   // TemporalAdjusters.next(DayOfWeek.TUESDAY))));
   }

}
