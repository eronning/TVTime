package edu.brown.cs.eronning.tvtime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.brown.cs.denj.data.Episode;

/**
 * An event, which includes an episode and a watching date/time.
 */
public class Event {
  private static final String DATE_FORMAT = "M/d/u";
  private static final String TIME_FORMAT = "h:mm a";
  Episode ep;
  String date;
  String time;

  public Event(Episode ep, LocalDateTime dateTime) {
    this.ep = ep;
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);// ("EEEE -- K:mm a");
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
    date = dateTime.format(dateFormat);
    time = dateTime.format(timeFormat);
  }

  /**
   * Gets the episode to be watched at this event.
   *
   * @return the episode.
   */
  public Episode getEp() {
    return ep;
  }

  /**
   * Gets the date of this event as a string.
   *
   * @return the date of the event.
   */
  public String getDate() {
    return date;
  }

  /**
   * Gets the time of this event as a string.
   *
   * @return the time of this event.
   */
  public String getTime() {
    return time;
  }

  public static void main(String[] args) {
    Event e = new Event(null, LocalDateTime.now());
  }
}
