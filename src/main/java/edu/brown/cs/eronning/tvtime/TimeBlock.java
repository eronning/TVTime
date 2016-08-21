package edu.brown.cs.eronning.tvtime;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class TimeBlock implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private int startIndex, endIndex;
  private String id;
  private int recurrences;

  public TimeBlock(int startIndex, int endIndex){
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.id = "";
    recurrences = 0;
  }

  public void setID(String id){
    this.id = id;
  }

  /**
   * Sets the number of times that this block will recur.
   * @param num the number of weeks ahead this time block will be scheduled.
   */
  public void setRecurrences(int num) {
    recurrences = num;
  }

  /**
   * Gets the number of recurrences of this time block.
   * @return number of weeks ahead
   */
  public int getRecurrences() {
    return recurrences;
  }

  public String getID(){
    return id;
  }

  public int getStartIndex(){
    return startIndex;
  }

  public int getEndIndex(){
    return endIndex;
  }

  public int getSize(){
    if(endIndex >= startIndex){
      return endIndex - startIndex + 1;
    }else{
      return (48 * 7 - startIndex) + endIndex + 1;
    }
  }

  /**
   * Gets the time of the start of this block's next occurrence after
   * the reference.
   * @param reference a reference time.
   * @return the time of the block's next occurrence.
   */
  public LocalDateTime nextStart(LocalDateTime reference) {
    //the occurrence of the time block during reference's week
    LocalDateTime thisWeek = getSunday(reference)
        .plusMinutes(startIndex*30);
    if (thisWeek.isBefore(reference)) {
      return thisWeek.plusWeeks(1); //add a week
    } else {
      return thisWeek;
    }
    //return (reference.plusHours((long) (startIndex/2.0)));
  }

  /**
   * Gets the time of the end of this block's next occurrence after
   * the reference.
   * @param reference a reference time.
   * @return the time of the block's next occurrence.
   */
  public LocalDateTime nextEnd(LocalDateTime reference) {
    //the occurrence of the time block during reference's week
    LocalDateTime thisWeek = getSunday(reference)
        .plusMinutes(endIndex*30);
    if (thisWeek.isBefore(reference)) {
      return thisWeek.plusWeeks(1); //add a week
    } else {
      return thisWeek;
    }
    //return (reference.plusHours((long) (startIndex/2.0)));
  }


//TODO delete
//  /**
//   * Gets the absolute start time of this time block.
//   * @param reference a reference time, to determine the week of the
//   * block.
//   * @return the date and time of the start of the time block.
//   */
//  public LocalDateTime getStartTime(LocalDateTime reference) {
//    //reference = getSunday(reference).withHour(0).withMinute(0);
//    return (getSunday(reference).plusHours((long) (startIndex/2.0)));
//    //TODO change for next occurrence / previous occurrence
//  }
//
//  /**
//   * Gets the absolute end time of this time block.
//   * @param reference a reference time, to determine the week of the
//   * block.
//   * @return the date and time of the end of the time block.
//   */
//  public LocalDateTime getEndTime(LocalDateTime reference) {
////    reference = reference
////        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
////        .withHour(0).withMinute(0);
//    return (getSunday(reference).plusHours((long) (endIndex/2.0)));
//  }

  private static LocalDateTime getSunday(LocalDateTime reference) {
    return reference
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        .withHour(0).withMinute(0);
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE -- K:mm a");
    LocalDateTime time = nextStart(LocalDateTime.now());
    return time.format(formatter);
  }

  public void setStartIndex(int start){
    startIndex = start;
  }
}
