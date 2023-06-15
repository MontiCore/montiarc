/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

import montiarc.rte.log.Log;

public class FloatOutPort extends NumericOutPort implements INumericOutPort {

  public FloatOutPort(String name) {
    super(name);
  }

  public FloatOutPort() {
    this("");
  }

  float value;

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    if (this.value != 0 && this.value != value) {
      Log.warn("Writing multiple times to port '" + this.getName() + "' in the same time slice. "
        + "Overriding '" + this.value + "' with '" + value + "'." );
    }
    this.value = value;
    this.sync();
  }

  /**
   * Sync this port with the observing input ports
   */
  @Override
  public void sync() {
    this.getObservers().forEach(o -> o.update(this.value));
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
