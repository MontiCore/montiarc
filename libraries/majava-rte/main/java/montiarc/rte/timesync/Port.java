/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public abstract class Port<T> implements IPortIn<T>, IPortOut<T> {

  protected T value;

  protected Boolean synced = false;

  protected String name = "";

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public Port(T value) {
    this.value = value;
  }

  public Port() {
    this(null);
  }

  @Override
  public abstract void setValue(T value);

  @Override
  public T getValue() {
    return this.value;
  }

  @Override
  public void setSynced(Boolean synced) {
    this.synced = synced;
  }

  @Override
  public Boolean isSynced() {
    return this.synced;
  }
}