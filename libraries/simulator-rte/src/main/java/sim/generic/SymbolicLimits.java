/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

public class SymbolicLimits<T> {

  private T lowerLimit;
  private T upperLimit;
  private String name;

  public SymbolicLimits(T upperLimit, T lowerLimit, String name) {
    this.lowerLimit = lowerLimit;
    this.upperLimit = upperLimit;
    this.name = name;
  }

  public T getLowerLimit() {
    return lowerLimit;
  }

  public T getUpperLimit() {
    return upperLimit;
  }
}
