/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.delegation;

/**
 * A port is a {@code DataSource}.
 *
 */
public class Port<T> extends DataSource<T> {
  
  public Port() {
    super();
  }
  
  public Port(T initialValue) {
    super(initialValue);
  }
}
