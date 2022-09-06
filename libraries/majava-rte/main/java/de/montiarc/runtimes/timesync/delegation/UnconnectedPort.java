/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.delegation;

/**
 * An unconnected port. This port is always synced.
 * Technically, this should not exist in a completed model, but the simulation should be robust anyway.
 */
public class UnconnectedPort<T> extends Port<T> {

  /**
   * Does nothing in this implementation.
   *
   * @param value ignored
   */
  @Override
  public void setValue(T value) {
    //NOP, this port type does not store values
  }

  /**
   * Called after completion of a computation cycle.
   */
  @Override
  public void update() {
    //NOP, this port type does nothing
  }

  /**
   * An unconnected port is always synced.
   *
   * @return {@code true}
   */
  @Override
  public Boolean isSynced() {
    return true;
  }
}