package edu.brown.cs.eronning.tvtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.denj.data.Show;

/**
 * This is my Scheduler class. It handles all of the scheduling in tvtime.
 *
 * @author yd17
 */
public class Scheduler {

  private final static int NUM_DAYS = 7;
  private final static int NUM_BLOCKS = 48;
  private final static int TOTAL_BLOCKS = NUM_DAYS * NUM_BLOCKS;

  public static boolean[] getCurrAvailTime
    (Map<Show, TimeBlock> schedule, boolean[] initAvailTime){

    boolean[] availTime = initAvailTime.clone();

    for(TimeBlock t: schedule.values()){
      for(int i= t.getStartIndex(); i <= t.getEndIndex(); i++){
        availTime[i] = false;
      }
    }
    return availTime;
  }

  /**
   * This is the schedule method.
   *
   * @param u1
   *          - user 1
   * @return mapping of show to time array, which consists of int day,
   *         startBlock and endBlock.
   */
  public static Map<Show, TimeBlock> schedule(User u1) {
    Map<Show, TimeBlock> toReturn = u1.getSchedule();
    boolean[] initAvailTime = u1.getAvailableTime();

    boolean[] availTime = getCurrAvailTime(toReturn, initAvailTime);

    List<TimeBlock> timeblocks = getTimeBlocks(availTime);
    int numTB = timeblocks.size();

    // shows are now sorted in descending order
    List<Show> shows = new ArrayList<>(u1.getShows());
    shows.sort(new showComparator());

    int i = 0;
    for (Show s : shows) {
      if(toReturn.containsKey(s)){
        continue;
      }
      if (i >= numTB) {
        continue;
      } else {
        TimeBlock t = timeblocks.get(i);
        int blocksNeeded = (int) Math.ceil(s.getRuntime() / 30.0);
        if (t.getSize() > blocksNeeded) {
          int endIndex =
            (t.getStartIndex() + blocksNeeded - 1) % (TOTAL_BLOCKS);
          TimeBlock newT = new TimeBlock(t.getStartIndex(), endIndex);
          newT.setID(s.getId());
          toReturn.put(s, newT);

          int newIndex = (t.getStartIndex() + blocksNeeded) % (TOTAL_BLOCKS);
          t.setStartIndex(newIndex);
          timeblocks.sort(new tbComparator());
        } else if (t.getSize() == blocksNeeded) {
          toReturn.put(s, t);
          t.setID(s.getId());
          i++;
        } else {
          continue;
        }
      }
    }
    u1.setSchedule(toReturn);
    return toReturn;
  }

  /**
   * This is my getTimeBlocks method.
   *
   * @param availTime
   *          - list of boolean arrays
   * @return list of timeblocks of available time.
   */
  public static List<TimeBlock> getTimeBlocks(boolean[] availTime) {

    List<TimeBlock> timeblocks = new ArrayList<TimeBlock>();
    int startIndex = 0; // first possible index

    int endIndex = TOTAL_BLOCKS - 1; // last possible index
    // Edge Case where start and end index are both true!
    if (availTime[startIndex] && availTime[endIndex]) {
      while (availTime[startIndex]) {
        startIndex++;
      }
      while (availTime[endIndex]) {
        endIndex--;
      }
      timeblocks.add(new TimeBlock(endIndex + 1, startIndex - 1));
    }

    for (int index = startIndex; index <= endIndex; index++) {
      if (availTime[index]) { // if true
        int first = index;
        if (index != endIndex) {
          while (availTime[index + 1]) {
            index++;
            if (index == endIndex) {
              break;
            }
          }
        }
        TimeBlock t = new TimeBlock(first, index);
        timeblocks.add(t);
      }
    }
    // return timeblocks in descending order
    timeblocks.sort(new tbComparator());

    return timeblocks;
  }

  /**
   * This is my getSharedTime method.
   *
   * @param list
   *          - list of users
   * @return boolean array, representing the shared available Time.
   */
  public static boolean[] getSharedTime(Collection<User> list) {
    boolean[] sharedTime = new boolean[TOTAL_BLOCKS];
    Arrays.fill(sharedTime, true);
    for (User u : list) {
      boolean[] userTime = u.getAvailableTime();
      for (int i = 0; i < TOTAL_BLOCKS; i++) {
        sharedTime[i] = (sharedTime[i] && userTime[i]);
      }
    }
    return sharedTime;
  }
  
  /**
   * This is my getTimeArray method.
   * @param list - list of users
   * @return int array, higher the number in a block means more users
   */
  public static int[] getTimeArray(List<User> list){
    int[] sharedTime = new int[TOTAL_BLOCKS];
    for(User u: list){
      boolean[] userTime = getCurrAvailTime(u.getSchedule(), u.getAvailableTime());
      for(int i = 0; i < TOTAL_BLOCKS; i++){
        if(userTime[i]){
          sharedTime[i]++;
        }
      }
    }
    return sharedTime;
  }

  public static Map<Integer, List<TimeBlock>> getCommonTimeblocks(List<User> list){
    Map<Integer, List<TimeBlock>> toReturn = new HashMap<Integer, List<TimeBlock>>();
    int numUsers = list.size();
    int[] timeArray = getTimeArray(list);

    for(int i = 1; i <= numUsers; i++){
      List<TimeBlock> toAdd = getTimeBlocks(timeArray, i);
      toReturn.put(i, toAdd);
    }
    return toReturn;
  }

  public static List<TimeBlock> getTimeBlocks(int[] timeArray, int num){
    List<TimeBlock> timeblocks = new ArrayList<TimeBlock>();
    for (int index = 0; index < TOTAL_BLOCKS; index++) {
      if (timeArray[index] >= num) { // if true
        int first = index;
        if (index != TOTAL_BLOCKS-1) {
          while (timeArray[index + 1] >= num) {
            index++;
            if (index == TOTAL_BLOCKS - 1) {
              break;
            }
          }
        }
        TimeBlock t = new TimeBlock(first, index);
        timeblocks.add(t);
      }
    }
    return timeblocks;
  }

  /**
   * Gets the amount of overlapping time among a collection of Users.
   * @param users the users to find overlapping time for.
   * @return the number of weekly half-hour time slots where all users
   * in the collection are free.
   */
  public static int getOverlap(Collection<User> users, int runtime) {
    boolean[] tb = getSharedTime(users);
    int numBlocksShared = 0;
    for (int i = 0; i < tb.length; i++) {
      if (tb[i]) {
        boolean blockCheck = true;
        double neededBlocks = Math.ceil(runtime/30.0);
        for (int consec = 0; consec <= neededBlocks - 1; consec++) {
          if (i + consec < tb.length) {
            blockCheck &= tb[i+consec];
          }
        }
        if (blockCheck) {
          numBlocksShared++;
        }

      }
    }
    return numBlocksShared;
  }

  /**
   * This is my getSharedShow method.
   *
   * @param list
   *          - list of users
   * @return a list of all shared shows among the users.
   */
  private static List<Show> getSharedShows(List<User> list) {
    List<Show> sharedShows = new ArrayList<Show>(list.get(0).getShows());
    for (int i = 1; i < list.size(); i++) {
      User u = list.get(i);
      sharedShows.retainAll(u.getShows());
    }
    return sharedShows;
  }

//  /**
//   * This is my scheudle method for a list of users.
//   *
//   * @param list
//   *          - list of users.
//   * @return a Map of shared shows to shared timeblocks
//   */
//  public static Map<Show, TimeBlock> schedule(List<User> list) {
//    boolean[] sharedTime = getSharedTime(list);
//    List<Show> sharedShows = getSharedShows(list);
//    if (sharedShows.size() == 0) {
//      return null;
//    }
//    User dummyUser = new User("name", "email", "password", "avatar");
//    dummyUser.setAvailableTime(sharedTime);
//    dummyUser.setShows(sharedShows);
//    return schedule(dummyUser);
//  }

  /**
   * This is the showComparator class. Returns show in descending order of show
   * runtime.
   *
   * @author yd17
   */
  private static class showComparator implements Comparator<Show> {
    @Override
    public int compare(Show s1, Show s2) {
      if (s1.getRuntime() > s2.getRuntime()) {
        return -1;
      } else if (s1.getRuntime() < s2.getRuntime()) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  /**
   * This is my tbComparator. Returns time Blocks in descending order of its
   * size.
   *
   * @author yd17
   */
  public static class tbComparator implements Comparator<TimeBlock> {
    @Override
    public int compare(TimeBlock t1, TimeBlock t2) {
      if (t1.getSize() > t2.getSize()) {
        return -1;
      } else if (t1.getSize() < t2.getSize()) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
