/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import java.util.Collection;
import java.util.HashSet;

public abstract class NumericOutPort extends PrimitivePort implements INumericOutPort {
  
  protected Collection<INumericInPort> observers;
  
  public Collection<INumericInPort> getObservers() {
    return observers;
  }
  
  public NumericOutPort(String name) {
    this.name = name;
    this.observers = new HashSet<>(1);
  }
  
  public NumericOutPort() {
    this("");
  }
  
  @Override
  public void connect(INumericInPort port) {
    this.getObservers().add(port);
  }
  
  @Override
  public void tick() {
    super.tick();
    this.getObservers().forEach(IPrimitivePort::tick);
  }
}
