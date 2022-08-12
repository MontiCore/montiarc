/* (c) https://github.com/MontiCore/monticore */
package arcbasis.timing;

import java.util.Objects;

/**
 * A model representing a Timing in MontiArc
 */
public class Timing {

  protected String name;

  Timing(String name) {
    this.name = name;
  }

  public static Timing untimed() {
    return new Timing("untimed");
  }

  public static Timing instant() {
    return new Timing("instant");
  }

  public static Timing delayed() {
    return new Timing("delayed");
  }

  public static Timing sync() {
    return new Timing("sync");
  }

  public static Timing causalsync() {
    return new Timing("causalsync");
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    // If the object is compared with itself then return true
    if (o == this) {
      return true;
    }

    if (!(o instanceof Timing)) {
      return false;
    }

    // typecast o
    Timing t = (Timing) o;

    // Compare the data members and return accordingly
    return Objects.equals(name, t.getName());
  }
}
