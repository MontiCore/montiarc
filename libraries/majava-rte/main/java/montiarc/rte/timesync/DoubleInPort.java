/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class DoubleInPort extends NumericInPort implements INumericInPort {

  public DoubleInPort(String name) {
    super(name);
  }

  public DoubleInPort() {
    super();
  }

  double value;

  public double getValue() {
    return value;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  @Override
  public void update(byte _byte) {
    this.update((double) _byte);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  @Override
  public void update(short _short) {
    this.update((double) _short);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  @Override
  public void update(int _int) {
    this.update((double) _int);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  @Override
  public void update(long _long) {
    this.update((double) _long);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  @Override
  public void update(float _float) {
    this.update((double) _float);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  @Override
  public void update(double _double) {
    this.value = _double;
    this.synced = true;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  @Override
  public void update(char _char) {
    this.update((double) _char);
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
