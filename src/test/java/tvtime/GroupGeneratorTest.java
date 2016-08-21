package tvtime;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import edu.brown.cs.eronning.schedule.GroupGenerator;
import edu.brown.cs.eronning.tvtime.User;

public class GroupGeneratorTest {

  User u1 = new User("u1", "u1", null, null);
  User u2 = new User("u2", "u2", null, null);
  User u3 = new User("u3", "u3", null, null);
  User u4 = new User("u4", "u4", null, null);
  User u5 = new User("u5", "u5", null, null);
  User u6 = new User("u6", "u6", null, null);
  User u7 = new User("u7", "u7", null, null);
  User u8 = new User("u8", "u8", null, null);


//  @BeforeClass
//  public static void setUpClass() {
//    tests[0] = new User("test1", "test1", null, null);
//    tests[1] = new User("test2", "test2", null, null);
//    tests[2] = new User("test3", "test3", null, null);
//    tests[3] = new User("test4", "test4", null, null);
//    tests[4] = new User("test5", "test5", null, null);
//    tests[5] = new User("test6", "test6", null, null);
//    tests[6] = new User("test7", "test7", null, null);
//
//  }

  @Test
  public void partitionsSmaller() {
    Set<User> partitions3 = new HashSet<User>();

    partitions3.add(u1);
    partitions3.add(u2);
    partitions3.add(u3);
    System.out.println("SIZE " + partitions3.size());

    Set<Set<Set<User>>> parts = GroupGenerator.getAllGroupings(partitions3);
    assertTrue(parts != null);
    assertTrue(parts.size() == 5);
    for (Set<Set<User>> s : parts) {
      int sum = 0;
      for (Set<User> subset : s) {
        sum += subset.size();
      }
      assertTrue(sum == 3);
    }

  }

  @Test
  public void partitonsLarger() {
    Set<User> partitions8 = new HashSet<User>();
    partitions8.add(u1);
    partitions8.add(u2);
    partitions8.add(u3);
    partitions8.add(u4);
    partitions8.add(u5);
    partitions8.add(u6);
    partitions8.add(u7);
    partitions8.add(u8);

    Set<Set<Set<User>>> parts = GroupGenerator.getAllGroupings(partitions8);
    assertTrue(parts.size() == 4140);
  }

  @Test
  public void oneMutualTime() {
    u1.setAvailableTime(1, true);
    u1.setAvailableTime(2, true);
    u1.setAvailableTime(17, true);
    u2.setAvailableTime(5, true);
    u2.setAvailableTime(6, true);
    u2.setAvailableTime(17, true);

    Set<User> testSet = new HashSet<User>();
    testSet.add(u1);
    testSet.add(u2);

    Set<Set<User>> groupedPart = new HashSet<Set<User>>();
    groupedPart.add(testSet);

    Set<Set<Set<User>>> bestGroups = GroupGenerator.suggestGroups(testSet, 30);
    assertTrue(bestGroups.contains(groupedPart));

  }


  @Test
  public void noTime() {
    u1.setAllFalse();
    u2.setAllFalse();
    u1.setAvailableTime(2, true);
    u2.setAvailableTime(17, true);

    Set<User> testSet = new HashSet<User>();
    testSet.add(u1);
    testSet.add(u2);

    Set<Set<Set<User>>> bestGroups = GroupGenerator.suggestGroups(testSet, 30);
    assertTrue(bestGroups.isEmpty());

  }

  @Test
  public void outlier() {
    u1.setAllFalse();
    u2.setAllFalse();
    u1.setAvailableTime(2, true);
    u2.setAvailableTime(2, true);
    u3.setAvailableTime(17, true);


    Set<User> testSet = new HashSet<User>();
    testSet.add(u1);
    testSet.add(u2);
    testSet.add(u3);

    Set<Set<User>> groupedPart = new HashSet<Set<User>>();
    Set<User> subgroup1 = ImmutableSet.of(u1, u2);
    Set<User> subgroup2 = ImmutableSet.of(u3);
    groupedPart.add(subgroup1);
    groupedPart.add(subgroup2);


    Set<Set<Set<User>>> bestGroups = GroupGenerator.suggestGroups(testSet, 30);
    assertTrue(!bestGroups.isEmpty());
    assertTrue(bestGroups.contains(groupedPart));

  }

  @Test
  public void split() {
    u1.setAllFalse();
    u2.setAllFalse();
    u3.setAllFalse();

    u1.setAvailableTime(2, true);
    u2.setAvailableTime(17, true);
    u3.setAvailableTime(2, true);
    u4.setAvailableTime(17, true);


    Set<User> testSet = new HashSet<User>();
    testSet.add(u1);
    testSet.add(u2);
    testSet.add(u3);
    testSet.add(u4);

    Set<Set<User>> groupedPart = new HashSet<Set<User>>();
    Set<User> subgroup1 = ImmutableSet.of(u1, u3);
    Set<User> subgroup2 = ImmutableSet.of(u2, u4);
    groupedPart.add(subgroup1);
    groupedPart.add(subgroup2);

    Set<Set<User>> notPresent = new HashSet<Set<User>>();
    Set<User> subgroup3 = ImmutableSet.of(u1, u2);
    Set<User> subgroup4 = ImmutableSet.of(u3, u4);
    groupedPart.add(subgroup3);
    groupedPart.add(subgroup4);


    Set<Set<Set<User>>> bestGroups = GroupGenerator.suggestGroups(testSet, 30);
    System.out.println(bestGroups);
    assertTrue(!bestGroups.isEmpty());
    //assertTrue(bestGroups.contains(groupedPart));
    assertTrue(!bestGroups.contains(notPresent));

  }

}
