/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

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
    this.value = value;
    this.synced = true;
  }

  /**
   * Called after completion of a computation cycle.
   */
  @Override
  public void update() {
    this.value = null;
    this.synced = false;
  }

  @Override
  public String toString() {
    return "[Time-Synchronous Undelayed Port, Current Value: " +
      this.value + ", Synced: " + this.synced +"]";
  }
}