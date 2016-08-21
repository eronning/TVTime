package tvtime;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.brown.cs.denj.data.Show;
import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.TimeBlock;
import edu.brown.cs.eronning.tvtime.User;

public class SchedulerTest {

  @Test
  public void simpleTest() {
    User u =
      new User("sample", "sampleEmail", "samplePassword", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    u.addShow(s);
    u.setAvailableTime(0, 0, true);
    u.setAvailableTime(0, 1, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 0);
    assertTrue(sTime.getEndIndex() == 1);
  }

  @Test
  public void noAvailTimeTest() {
    User u =
      new User("sample", "sampleEmail", "samplePassword", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    u.addShow(s);
    u.setAvailableTime(5, 1, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u);
    assertNull(results.get(s));
  }

  @Test
  public void twoShowTest() {
    User u =
      new User("sample", "sampleEmail", "samplePassword", "sampleAvatar");
    Show s1 = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    Show s2 = new Show("BBT", 7, false, "image", "id2", "url", 30, null);
    u.addShow(s1);
    u.addShow(s2);
    u.setAvailableTime(1, 5, true);
    u.setAvailableTime(1, 6, true);
    u.setAvailableTime(2, 13, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u);
    TimeBlock s1Time = results.get(s1);
    assertTrue(s1Time.getStartIndex() == 53);
    assertTrue(s1Time.getEndIndex() == 54);
    TimeBlock s2Time = results.get(s2);
    assertTrue(s2Time.getStartIndex() == 109);
    assertTrue(s2Time.getEndIndex() == 109);
  }

  @Test
  public void bigTimeBlockTest() {
    User u =
      new User("sample", "sampleEmail", "samplePassword", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    Show s2 = new Show("HoC", 3, false, "image", "id2", "url", 60, null);
    u.addShow(s);
    u.addShow(s2);
    u.setAvailableTime(1, 5, true);
    u.setAvailableTime(1, 6, true);
    u.setAvailableTime(1, 7, true);
    u.setAvailableTime(1, 8, true);
    u.setAvailableTime(2, 9, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 55);
    assertTrue(sTime.getEndIndex() == 56);
    TimeBlock s2Time = results.get(s2);
    assertTrue(s2Time.getStartIndex() == 53);
    assertTrue(s2Time.getEndIndex() == 54);
  }

  @Test
  public void timeBlockSwitchOrderTest() {
    User u =
      new User("sample", "sampleEmail", "samplePassword", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    Show s2 = new Show("Spring Weekend", 3, false, "image", "id2", "url", 30, null);
    u.addShow(s);
    u.addShow(s2);
    u.setAvailableTime(1, 5, true);
    u.setAvailableTime(1, 6, true);
    u.setAvailableTime(1, 7, true);
    u.setAvailableTime(1, 8, true);
    u.setAvailableTime(2, 6, true);
    u.setAvailableTime(2, 7, true);
    u.setAvailableTime(2, 8, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 53);
    assertTrue(sTime.getEndIndex() == 54);
    TimeBlock s2Time = results.get(s2);
    assertTrue(s2Time.getStartIndex() == 102);
    assertTrue(s2Time.getEndIndex() == 102);
  }

  @Test
  public void twoUserTest() {
    User u1 =
      new User("sample1", "sampleEmail1", "samplePassword1", "sampleAvatar");
    User u2 =
      new User("sample2", "sampleEmail2", "samplePassword2", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    u1.addShow(s);
    u2.addShow(s);
    u1.setAvailableTime(0, 1, true);
    u1.setAvailableTime(0, 2, true);
    u2.setAvailableTime(0, 1, true);
    u2.setAvailableTime(0, 2, true);
    List<User> toSearch = new ArrayList<User>();
    toSearch.add(u1);
    toSearch.add(u2);
//    Map<Show, TimeBlock> results = Scheduler.schedule(toSearch);
//    TimeBlock sTime = results.get(s);
//    assertTrue(sTime.getStartIndex() == 1);
//    assertTrue(sTime.getEndIndex() == 2);
  }

  @Test
  public void overTwoDayTest() {
    User u1 =
      new User("sample1", "sampleEmail1", "samplePassword1", "sampleAvatar");
    Show s = new Show("GoT", 5, false, "image", "id1", "url", 55, null);
    u1.addShow(s);
    u1.setAvailableTime(0, 47, true);
    u1.setAvailableTime(1, 0, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u1);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 47);
    assertTrue(sTime.getEndIndex() == 48);
  }

  @Test
  public void SaturdayToSundayTest() {
    User u1 =
      new User("sample1", "sampleEmail1", "samplePassword1", "sampleAvatar");
    Show s = new Show("really long show", 5, false, "image", "id1", "url", 120, null);
    u1.addShow(s);
    u1.setAvailableTime(6, 46, true);
    u1.setAvailableTime(6, 47, true);
    u1.setAvailableTime(0, 0, true);
    u1.setAvailableTime(0, 1, true);
    u1.setAvailableTime(3, 12, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u1);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 334);
    assertTrue(sTime.getEndIndex() == 1);
    assertTrue(sTime.getSize() == 4);
  }

  @Test
  public void lastBlockSplitTest() {
    User u1 = new User("sample", "email", "password", "sampleAvatar");
    Show s = new Show("show", 5, false, "image", "id", "url", 30, null);
    Show s2 = new Show("show2", 2, false, "image", "id2", "url", 61, null);
    u1.addShow(s);
    u1.addShow(s2);
    u1.setAvailableTime(6, 46, true);
    u1.setAvailableTime(6, 47, true);
    u1.setAvailableTime(0, 0, true);
    u1.setAvailableTime(0, 1, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u1);
    TimeBlock s2Time = results.get(s2);
    assertTrue(s2Time.getStartIndex() == 334);
    assertTrue(s2Time.getEndIndex() == 0);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 1);
    assertTrue(sTime.getEndIndex() == 1);
  }

  @Test
  public void lastBlockTest() {
    User u1 = new User("sample", "email", "password", "sampleAvatar");
    Show s = new Show("show", 5, false, "image", "id", "url", 30, null);
    u1.addShow(s);
    u1.setAvailableTime(6, 47, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u1);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 335);
    assertTrue(sTime.getEndIndex() == 335);
  }

  @Test
  public void lastTwoBlockTest() {
    User u1 = new User("sample", "email", "password", "sampleAvatar");
    Show s = new Show("show", 5, false, "image", "id", "url", 31, null);
    u1.addShow(s);
    u1.setAvailableTime(6, 46, true);
    u1.setAvailableTime(6, 47, true);
    Map<Show, TimeBlock> results = Scheduler.schedule(u1);
    TimeBlock sTime = results.get(s);
    assertTrue(sTime.getStartIndex() == 334);
    assertTrue(sTime.getEndIndex() == 335);
  }
  
  @Test
  public void simpleTimeBlockTest(){
    int[] timeArray = new int[7 * 48];
    timeArray[1] = 1;
    timeArray[2] = 2;
    timeArray[3] = 1;
    List<TimeBlock> list = Scheduler.getTimeBlocks(timeArray, 1);
    assertTrue(list.size() == 1);
    assertTrue(list.get(0).getStartIndex() == 1);
    assertTrue(list.get(0).getEndIndex() == 3);
    List<TimeBlock> list2 = Scheduler.getTimeBlocks(timeArray, 2);
    assertTrue(list2.size() == 1);
    assertTrue(list2.get(0).getStartIndex() == 2 );
    assertTrue(list2.get(0).getEndIndex() == 2);
  }
  
  @Test
  public void TimeBlockTest(){
    int[] timeArray = new int[7*48];
    timeArray[0] = 1;
    timeArray[1] = 5;
    timeArray[2] = 4;
    timeArray[3] = 3;
    timeArray[5] = 1;
    timeArray[6] = 1;
    timeArray[7] = 1;
    List<TimeBlock> list = Scheduler.getTimeBlocks(timeArray, 1);
    assertTrue(list.size() == 2);
    List<TimeBlock> list2 = Scheduler.getTimeBlocks(timeArray, 2);
    assertTrue(list2.size() == 1);
    List<TimeBlock> list3 = Scheduler.getTimeBlocks(timeArray, 3);
    assertTrue(list3.get(0).getStartIndex() == 1);
    assertTrue(list3.get(0).getEndIndex() == 3);
  }
  
  @Test
  public void getCommonTimeBlockTest(){
    User u1 = new User("erik", "erik@erik.com", "erik", "erik");
    for(int i = 6; i<= 12; i++){
      u1.setAvailableTime(i, true);
    }
    for(int i = 50; i <= 55; i++){
      u1.setAvailableTime(i, true);
    }
    User u2 = new User("jaclyn", "jaclyn@jaclyn.com", "jaclyn", "jaclyn");
    for(int i = 10; i<= 12; i++){
      u2.setAvailableTime(i, true);
    }
    for(int i = 49; i<= 51; i++){
      u2.setAvailableTime(i, true);
    }
    List<User> toSearch = new ArrayList<User>();
    toSearch.add(u1);
    toSearch.add(u2);
    Map<Integer, List<TimeBlock>> results = Scheduler.getCommonTimeblocks(toSearch);
    List<TimeBlock> list1 = results.get(1);
    assertTrue(list1.size() == 2);
    List<TimeBlock> list2 = results.get(2);
    assertTrue(list2.get(0).getStartIndex() == 10);
    assertTrue(list2.get(0).getEndIndex() == 12);
  }
  
}
