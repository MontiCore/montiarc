/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public abstract class NumericInPort extends PrimitivePort implements INumericInPort {
  protected boolean synced;
  
  public NumericInPort(String name) {
    this.name = name;
    this.synced = false;
  }
  
  public NumericInPort() {
    this("");
  }
  
  @Override
  public boolean isSynced() {
    return this.synced;
  }
  
  @Override
  public void tick() {
    super.tick();
    this.synced = false;
  }
}
