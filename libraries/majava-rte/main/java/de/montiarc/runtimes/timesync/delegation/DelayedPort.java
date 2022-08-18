/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.delegation;

/**
 * A port which passes on values with a delay of one computation cycle.
 */
public class DelayedPort<T> extends Port<T> {

  protected T nextValue;

  public DelayedPort(T currentValue) {
    super(currentValue);
    nextValue = null;
    synced = true;
  }

  public DelayedPort() {
    this(null);
  }

  /**
   * Stores the given value to be put on the port after the next call to {@link #update()}.
   */
  @Override
  public void setValue(T value) {
    nextValue = value;
  }

  /**
   * Called after completion of a computation cycle.
   */
  @Override
  public void update() {
    currentValue = nextValue;
    nextValue = null;
    synced = true;
  }

  @Override
  public String toString() {
    return "[Time-Synchronous Delayed Port, Current Value: " +
      currentValue + ", Next Value: " + nextValue + ", Synced: " + synced + "]";
  }
}