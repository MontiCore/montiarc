/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import montiarc.rte.log.Log;

import java.util.Collection;
import java.util.HashSet;

public class BooleanOutPort extends PrimitivePort {
  
  protected Collection<BooleanInPort> observers;
  protected boolean value;
  
  public BooleanOutPort(String name) {
    this.name = name;
    this.observers = new HashSet<>(1);
  }
  
  public BooleanOutPort() {
    this("");
  }
  
  public void setValue(boolean value) {
    if (this.value != false && this.value != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice. "
          + "Overriding '" + this.value + "' with '" + value + "'." );
    }
    this.value = value;
    this.sync();
  }
  
  public boolean getValue() {
    return this.value;
  }
  
  @Override
  protected void resetValue() {
    this.value = false;
  }
  
  public Collection<BooleanInPort> getObservers() {
    return observers;
  }
  
  public void connect(BooleanInPort port) {
    this.getObservers().add(port);
  }
  
  @Override
  public void tick() {
    super.tick();
    this.getObservers().forEach(IPrimitivePort::tick);
  }
  
  public void sync() {
    this.getObservers().forEach(o -> o.update(this.value));
  }
}
