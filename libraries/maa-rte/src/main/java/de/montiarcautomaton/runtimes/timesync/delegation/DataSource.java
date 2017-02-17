package de.montiarcautomaton.runtimes.timesync.delegation;

/**
 * A data source for a component is either a port or a variable.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public abstract class DataSource<T> {
  
  protected T currentValue = null;
  protected T nextValue = null;
  
  public DataSource(T initialValue) {
    this.currentValue = initialValue;
    this.nextValue = null;
  }
  
  public DataSource() {
    this.currentValue = null;
    this.nextValue = null;
  }
  
  public T getCurrentValue() {
    return currentValue;
  }
  
  public void setNextValue(T nextValue) {
    this.nextValue = nextValue;
  }
  
  public T getNextValue() {
    return this.nextValue;
  }
  
  public void update() {
    this.currentValue = this.nextValue;
    this.nextValue = null;
  }
  
  @Override
  public String toString() {
    return "[cur: " + this.getCurrentValue() + ", nxt: " + this.nextValue + "]";
  }
}