/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class ShortInPort extends NumericInPort implements INumericInPort {

  public ShortInPort(String name) {
    super(name);
  }

  public ShortInPort() {
    super();
  }

  short value;

  public short getValue() {
    return value;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  @Override
  public void update(byte _byte) {
    this.update((short) _byte);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  @Override
  public void update(short _short) {
    this.value = _short;
    this.synced = true;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  @Override
  public void update(int _int) {
    this.update((short) _int);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  @Override
  public void update(long _long) {
    this.update((short) _long);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  @Override
  public void update(float _float) {
    this.update((short) _float);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  @Override
  public void update(double _double) {
    this.update((short) _double);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  @Override
  public void update(char _char) {
    this.update((short) _char);
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
