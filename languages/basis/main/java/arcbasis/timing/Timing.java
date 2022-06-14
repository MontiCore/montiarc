/* (c) https://github.com/MontiCore/monticore */
package arcbasis.timing;

/**
 * A model representing a Timing in MontiArc
 */
public class Timing {

  protected String name;

  Timing(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
