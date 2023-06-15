/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import montiarc.rte.log.Log;

public class BooleanDelayPort extends BooleanOutPort {
  
  protected boolean nextValue;
  
  public BooleanDelayPort(String name) {
    super(name);
    this.nextValue = false;
  }
  
  public BooleanDelayPort() {
    this("");
  }
  
  @Override
  public void setValue(boolean value) {
    if(this.nextValue != false && this.nextValue != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice.");
    }
    this.nextValue = value;
  }
  
  @Override
  public void tick() {
    super.tick();
    this.value = nextValue;
    this.nextValue = false;
    this.sync();
  }
}
