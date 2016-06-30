package de.monticore.lang.montiarc.tag.drawing;

/**
 * Created by Michael von Wenckstern on 04.06.2016.
 */
public class IdGenerator {
  protected static int id = 0;

  /**
   * returns unique ID, e.g in one SVG file
   */
  public static int getUniqueId() {
    return id++;
  }
}
