/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.delegation;

public class UndelayedPort<T> extends Port<T> {

  public UndelayedPort(T currentValue) {
    super(currentValue);
  }

  public UndelayedPort() {
    this(null);
  }

  /**
   * Sets the current value to the given value.
   * Also sets {@link #synced} to true.
   */
  @Override
  public void setValue(T value) {
    currentValue = value;
    synced = true;
  }

  /**
   * Called after completion of a computation cycle.
   */
  @Override
  public void update() {
    currentValue = null;
    synced = false;
  }

  @Override
  public String toString() {
    return "[Time-Synchronous Undelayed Port, Current Value: " +
      currentValue + ", Synced: " + synced +"]";
  }
}