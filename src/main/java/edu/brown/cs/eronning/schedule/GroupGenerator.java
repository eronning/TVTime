package edu.brown.cs.eronning.schedule;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.brown.cs.eronning.tvtime.Scheduler;
import edu.brown.cs.eronning.tvtime.User;

public class GroupGenerator {

  private static final int NUM_SUGGESTIONS = 3;

  /**
   * Produces all partitions of a set of Strings.
   * @param all the set of all user IDs
   * @return the set of all partitions of the given set.
   */
  public static Set<Set<Set<User>>> getAllGroupings(Set<User> all) {
    if (all.size() > 8) {
      throw new IllegalArgumentException("Group of size " + all.size()
          + " is too large to determine partitions");
    }

    Set<Set<Set<User>>> allGroupings = new HashSet<Set<Set<User>>>();

    if (all.isEmpty()) {
      //Set<Set<String>> toAdd =
      allGroupings.add(new HashSet<Set<User>>()); //base case an empty partition
      return allGroupings;
    }

    User first = null;
    for (User usr : all) {
      first = usr;
      all.remove(usr);
      break;
    }

    Set<Set<Set<User>>> recursiveOut = getAllGroupings(all);

    for (Set<Set<User>> partition : recursiveOut) {
      Set<User> toAppend = new HashSet<User>();
      toAppend.add(first);

      Set<Set<User>> newPartition = new HashSet<Set<User>>();
      newPartition.addAll(partition);
      newPartition.add(toAppend);
      allGroupings.add(newPartition);

      for (Set<User> subset : partition) {
        Set<Set<User>> partition2 = new HashSet<Set<User>>();
        Set<User> newSubset = new HashSet<User>();
        newSubset.addAll(subset);
        newSubset.add(first);
        partition2.addAll(partition);
        partition2.remove(subset);
        partition2.add(newSubset);
        allGroupings.add(partition2);
      }
    }
    return allGroupings;
  }

  /**
   * Makes suggestions for partitioning a group of people
   * based on availability.
   * @param users the collection of users to partition
   * @param runtime the runtime of the episode to schedule
   * @return a set of partitions of users.
   */
  public static Set<Set<Set<User>>> suggestGroups(Collection<User> users,
      int runtime) {
    //disallow groups larger than 8?
    Set<User> all = new HashSet<User>();
    all.addAll(users);
    Set<Set<Set<User>>> partitions = getAllGroupings(all);
    //GroupComparator comp = new GroupComparator();
    PriorityQueue<Set<Set<User>>> groupQueue =
        new PriorityQueue<Set<Set<User>>>(new GroupComparator(runtime));
    for (Set<Set<User>> partition : partitions) {
      groupQueue.add(partition);
    }
    Set<Set<Set<User>>> toReturn = new HashSet<Set<Set<User>>>();
    for (int i = 0; i < NUM_SUGGESTIONS; i++) {
      if (!groupQueue.isEmpty()) {
        Set<Set<User>> group = groupQueue.remove();
        if (group.size() == users.size()) {
          i--;
          continue;
        }
        boolean conflict = false;
        for (Set<User> subgroup : group) {
          if (subgroup.size() > 1 &&
              Scheduler.getOverlap(subgroup, runtime) == 0) {
            conflict = true;
          }
        }
        if (!conflict) {
          toReturn.add(group);
        } else {
          i--;
        }
      } else {
        break;
      }
    }
    return toReturn;
  }

  //Not currently using this...
  public static Set<Set<String>> getAllSubsets(Set<String> set) {
    Set<Set<String>> toReturn = new HashSet<Set<String>>();
    toReturn.add(new HashSet<String>());
    for (String i : set) {
      for (Set<String> subset : toReturn) {
        Set<String> toAdd = new HashSet<String>();
        for (String subsetInt : subset) {
          toAdd.add(subsetInt);
        }
        toAdd.add(i);
        toReturn.add(toAdd);
      }
    }
    return toReturn;
  }

  public static void main(String[] args) {
//    User[] tests = new User[7];
//    tests[0] = new User("test1", "test1", null, null);
//    tests[1] = new User("test2", "test2", null, null);
//    tests[2] = new User("test3", "test3", null, null);
//    tests[3] = new User("test4", "test4", null, null);
//    tests[4] = new User("test5", "test5", null, null);
//    tests[5] = new User("test6", "test6", null, null);
//    tests[6] = new User("test7", "test7", null, null);

    User user1 = new User("test1", "test1", null, null);
    User user2 = new User("test2", "test2", null, null);
    User user3 = new User("test3", "test3", null, null);

//    user1.setAvailableTime(1, true);
//    user2.setAvailableTime(1, true);
//    user1.setAvailableTime(0, true);
//    user2.setAvailableTime(2, true);
//    user3.setAvailableTime(11, true);
//    user3.setAvailableTime(12, true);
//    user3.setAvailableTime(2, true);

    user1.setAvailableTime(15, true);
    user1.setAvailableTime(16, true);
    user1.setAvailableTime(17, true);
    user2.setAvailableTime(16, true);
    user2.setAvailableTime(19, true);
//    user3.setAvailableTime(2, true);
//    user3.setAvailableTime(3, true);
//    user3.setAvailableTime(12, true);


//    for (int i = 0; i < tests.length-3; i++) {
//      //tests[i].setAllFalse();
//
//      tests[i].setAvailableTime(0, true);
//      tests[i].setAvailableTime(1, true);
//      tests[i].setAvailableTime(2, true);
//      tests[i].setAvailableTime(3, true);
//      tests[i].setAvailableTime(4, true);
//      tests[i].setAvailableTime(5, true);
//    }
//
//    tests[4].setAvailableTime(10,true);
//    tests[5].setAvailableTime(10,true);

    Set<User> testSet = new HashSet<User>();
    testSet.add(user1);
    testSet.add(user2);
    //testSet.add(user3);
//    for(User u : tests) {
//      testSet.add(u);
//    }

    Set<Set<Set<User>>> partitions = suggestGroups(testSet, 60);
    for (Set<Set<User>> lol : partitions) {
      System.out.println(lol);
      int numShared = 0;
      int sharedProduct = 1;
      for (Set<User> subset : lol) {
        int thing = Scheduler.getSharedTime(subset).length;
        numShared += thing;
        sharedProduct *= thing;
      }
      //System.out.println(numShared + " " + sharedProduct);
    }

  }

  /**
   * Compares different partitions of users.
   * First prioritizes partitions where there is some mutually available time
   * for all subsets.
   * Next, prioritizes larger and more evenly sized groups.
   * As a tie-breaker, prefers partitions where subgroups have
   * more mutually available time.
   *
   */
  private static class GroupComparator implements Comparator<Set<Set<User>>> {
    //The amount of time of the episode
    private int runtime;

    public GroupComparator(int runtime) {
      this.runtime = runtime;
    }

    @Override
    public int compare(Set<Set<User>> o1, Set<Set<User>> o2) {
      //comparing partitions to maximize shared time
      DescriptiveStatistics stats1 = new DescriptiveStatistics();
      DescriptiveStatistics stats2 = new DescriptiveStatistics();

      int numSlots1 = 0;
      int notAvailable1 = 0;
      int slotsProduct1 = 1;

      int numSlots2 = 0;
      int notAvailable2 = 0;
      int slotsProduct2 = 1;

      for (Set<User> partition : o1) {
        stats1.addValue(partition.size());
        int sharedTime = Scheduler.getOverlap(partition, runtime)
            * (partition.size());
        numSlots1 += sharedTime;
        if (sharedTime == 0) {
          notAvailable1 += partition.size();
        }
        slotsProduct1 *= sharedTime;
      }
      for (Set<User> partition : o2) {
        stats2.addValue(partition.size());
        int sharedTime = Scheduler.getOverlap(partition, runtime)
            * (partition.size());
        numSlots2 += sharedTime;
        if (sharedTime == 0) {
          notAvailable2 += partition.size();
        }
        slotsProduct2 *= sharedTime;
        //numSlots2 += Scheduler.getSharedTime(partition).length;
      }

      int avgComp = Double.compare(stats2.getMean(), stats1.getMean());
      int sizeComp = Double.compare(stats1.getStandardDeviation(),
          stats2.getStandardDeviation());
      int overlapComp = Integer.compare(numSlots2, numSlots1);

      //Groups where everyone can be scheduled are preferred
      //This will first minimize the number of people in conflicting groups.
      if (slotsProduct1 == 0 && slotsProduct2 == 0) {
        int compConflicts = Integer.compare(notAvailable1, notAvailable2);
        if (compConflicts == 0) {
          //this case involves isolating the "problem people"
          //so it prefers larger groups with good overlap
          return 2*avgComp + overlapComp;
        } else {
          return compConflicts;
        }
      }
      if (slotsProduct1 == 0) {
        return 1;
      }
      if (slotsProduct2 == 0) {
        return -1;
      }

      //order by : fewer groups > even-sized group > groups with more overlap
      if (avgComp == 0) {
        if (sizeComp == 0) {
          return overlapComp;
        } else {
          return sizeComp;
        }
      } else {
        return avgComp;
      }
      //return sizeComp + 2*avgComp + 2*overlapComp; TODO tweak or something


    }


  }

}
