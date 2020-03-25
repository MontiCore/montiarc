/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.runtimes.timesync.delegation;

/**
 * A variable is a {@code DataSource}.
 *
 */
public class Variable<T> extends DataSource<T> {
  
  public Variable() {
    super();
  }
  
  public Variable(T initialValue) {
    super(initialValue);
  }
  
  @Override
  public void update() {
    if (this.nextValue != null) {
      this.currentValue = this.nextValue;
      this.nextValue = null;
    }
  }
}
