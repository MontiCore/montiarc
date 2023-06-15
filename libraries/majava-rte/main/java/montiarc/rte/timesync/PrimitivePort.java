/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public abstract class PrimitivePort implements IPrimitivePort {
  
  protected String name;
  
  public String getName() {
    return name;
  }
  
  protected abstract void resetValue();
  
  public void tick() {
    resetValue();
  }
}
