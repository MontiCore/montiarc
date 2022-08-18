/* (c) https://github.com/MontiCore/monticore */
package arcbasis.timing;

import java.util.Objects;

/**
 * A model representing a Timing in MontiArc
 */
public enum Timing {
  UNTIMED("untimed"),
  INSTANT("instant"),
  DELAYED("delayed"),
  SYNC("sync"),
  CAUSALSYNC("causalsync");

  private final String name;

  Timing(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
