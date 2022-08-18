/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.delegation;

public abstract class Port<T> {

  protected T currentValue;

  protected Boolean synced = false;

  public Port(T currentValue) {
    this.currentValue = currentValue;
  }

  public Port() {
    this(null);
  }

  /**
   * This method should be used to set / update the port value
   * in a way that is sensible with regard to the concrete port implementation.
   * <br>
   * For an instantly timed port, this may update the current value,
   * for a delayed port it may save the given value in a buffer variable.
   */
  public abstract void setValue(T value);

  /**
   * Called after completion of a computation cycle.
   */
  public abstract void update();

  public T getCurrentValue() {
    return this.currentValue;
  }

  public void setSynced(Boolean synced) {
    this.synced = synced;
  }

  public Boolean isSynced() {
    return this.synced;
  }
}