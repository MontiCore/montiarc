/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public abstract class Port<T> implements IPort<T> {

  protected T value;

  protected String name;

  protected String getName() {
    return this.name;
  }

  @Override
  public T getValue() {
    return this.value;
  }

  @Override
  public void tick() {
    this.logValue();
    this.value = null;
  }

  protected void logValue() {
    montiarc.rte.log.Log.trace("Value of port '" + this.getName() + "' = " + this.getValue());
  }
}
