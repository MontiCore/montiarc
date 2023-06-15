/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class BooleanInPort extends PrimitivePort {
  
  protected boolean synced;
  protected boolean value;
  
  public BooleanInPort(String name) {
    this.name = name;
    this.synced = false;
  }
  
  public BooleanInPort() {
    this("");
  }
  
  public boolean getValue() {
    return this.value;
  }
  
  @Override
  protected void resetValue() {
    this.value = false;
  }
  
  public void update(boolean _boolean) {
    this.value = _boolean;
    this.synced = true;
  }
  
  public boolean isSynced() {
    return this.synced;
  }
  
  @Override
  public void tick() {
    super.tick();
    this.synced = false;
  }
}
