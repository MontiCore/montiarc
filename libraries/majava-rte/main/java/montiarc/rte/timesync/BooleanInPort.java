/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import java.util.Collection;
import java.util.HashSet;

public class BooleanInPort extends PrimitivePort {

  protected Collection<BooleanInPort> observers;

  public Collection<BooleanInPort> getObservers() {
    return observers;
  }
  
  protected boolean synced;
  protected boolean value;
  
  public BooleanInPort(String name) {
    this.name = name;
    this.synced = false;
    this.observers = new HashSet<>(1);
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
    this.getObservers().forEach(p -> p.update(_boolean));
    this.synced = true;
  }
  
  public boolean isSynced() {
    return this.synced;
  }
  
  @Override
  public void tick() {
    super.tick();
    this.getObservers().forEach(BooleanInPort::tick);
    this.synced = false;
  }

  public void connect(BooleanInPort port) {
    this.getObservers().add(port);
  }
}
