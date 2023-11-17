/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import java.util.Collection;
import java.util.HashSet;

public abstract class NumericInPort extends PrimitivePort implements INumericInPort {

  protected Collection<INumericInPort> observers;

  public Collection<INumericInPort> getObservers() {
    return observers;
  }

  protected boolean synced;
  
  public NumericInPort(String name) {
    this.name = name;
    this.synced = false;
    this.observers = new HashSet<>(1);
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
    this.getObservers().forEach(IPrimitivePort::tick);
    this.synced = false;
  }

  @Override
  public void connect(INumericInPort port) {
    this.getObservers().add(port);
  }
}
