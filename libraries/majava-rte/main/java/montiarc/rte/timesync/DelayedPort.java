/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

/**
 * A port which passes on values with a delay of one computation cycle.
 */
public class DelayedPort<T> extends Port<T> {

  protected T nextValue;

  public DelayedPort(T value) {
    super(value);
    this.nextValue = null;
    this.synced = true;
  }

  public DelayedPort() {
    this(null);
  }

  /**
   * Stores the given value to be put on the port after the next call to {@link #update()}.
   */
  @Override
  public void setValue(T value) {
    if(this.value != null && this.value != value) {
      montiarc.rte.log.Log.warn("Writing multiple times to port '" + this.getName() + "' in the same computation cycle.");
    }
    this.nextValue = value;
  }

  /**
   * Called after completion of a computation cycle.
   */
  @Override
  public void update() {
    this.value = this.nextValue;
    this.nextValue = null;
    this.synced = true;
  }

  @Override
  public String toString() {
    return "[Time-Synchronous Delayed Port, Current Value: " +
      this.value + ", Next Value: " + this.nextValue + ", Synced: " + this.synced + "]";
  }
}