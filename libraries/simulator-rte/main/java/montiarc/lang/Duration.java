/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

public class Duration extends DurationTOP {

  public Duration(long milliseconds) {
    this.milliseconds = milliseconds;
  }

  @Override
  public long getInMilliseconds() {
    return milliseconds;
  }

  @Override
  public Duration add(Duration other) {
    return new Duration(milliseconds + other.milliseconds);
  }

  @Override
  public Duration subtract(Duration other) {
    if (milliseconds < other.milliseconds) {
      return new Duration(0);
    }
    return new Duration(milliseconds - other.milliseconds);
  }

  public static Duration ofMilliseconds(long milliseconds) {
    return new Duration(milliseconds);
  }

  public static Duration ofSeconds(long seconds) {
    return new Duration(seconds * 1000);
  }

  public static Duration ofSeconds(long seconds, long milliseconds) {
    return new Duration(seconds * 1000 + milliseconds);
  }

  public static Duration ofMinutes(long minutes) {
    return new Duration(minutes * 60 * 1000);
  }

  public static Duration ofMinutes(long minutes, long seconds) {
    return new Duration(minutes * 60 * 1000 + seconds * 1000);
  }

  public static Duration ofMinutes(long minutes, long seconds, long milliseconds) {
    return new Duration(minutes * 60 * 1000 + seconds * 1000 + milliseconds);
  }

  public static Duration ofHours(long hours) {
    return new Duration(hours * 60 * 60 * 1000);
  }

  public static Duration ofHours(long hours, long minutes) {
    return new Duration(hours * 60 * 60 * 1000 + minutes * 60 * 1000);
  }

  public static Duration ofHours(long hours, long minutes, long seconds) {
    return new Duration(hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000);
  }

  public static Duration ofHours(long hours, long minutes, long seconds, long milliseconds) {
    return new Duration(hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + milliseconds);
  }

  public static Duration ofDays(long days) {
    return new Duration(days * 24 * 60 * 60 * 1000);
  }

  public static Duration ofDays(long days, long hours) {
    return new Duration(days * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000);
  }

  public static Duration ofDays(long days, long hours, long minutes) {
    return new Duration(days * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000 + minutes * 60 * 1000);
  }

  public static Duration ofDays(long days, long hours, long minutes, long seconds) {
    return new Duration(days * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000);
  }

  public static Duration ofDays(long days, long hours, long minutes, long seconds, long milliseconds) {
    return new Duration(days * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + milliseconds);
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof Duration) && ((Duration) o).milliseconds == milliseconds;
  }
}
