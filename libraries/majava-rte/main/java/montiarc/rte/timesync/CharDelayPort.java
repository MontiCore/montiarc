/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import montiarc.rte.log.Log;

public class CharDelayPort extends CharOutPort implements INumericOutPort {

  protected char nextValue;

  public CharDelayPort(String name) {
    super(name);
    this.nextValue = 0;
  }

  public CharDelayPort() {
    this("");
  }

  @Override
  public void setValue(char value) {
    if(this.nextValue != 0 && this.nextValue != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice.");
    }
    this.nextValue = value;
  }

  @Override
  public void tick() {
    super.tick();
    this.value = nextValue;
    this.nextValue = 0;
    this.sync();
  }
}
