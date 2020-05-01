/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.runtimes.timesync.delegation;

/**
 * A port is a {@code DataSource}.
 *
 */
public class Port<T> extends DataSource<T> {
  // empty port with no connection
  public static final Port EMPTY = new Port<>();
  
  public Port() {
    super();
  }
  
  public Port(T initialValue) {
    super(initialValue);
  }
}
