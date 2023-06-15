/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import montiarc.rte.log.Log;

public class ByteDelayPort extends ByteOutPort implements INumericOutPort {

  protected byte nextValue;

  public ByteDelayPort(String name) {
    super(name);
    this.nextValue = 0;
  }

  public ByteDelayPort() {
    this("");
  }

  @Override
  public void setValue(byte value) {
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
