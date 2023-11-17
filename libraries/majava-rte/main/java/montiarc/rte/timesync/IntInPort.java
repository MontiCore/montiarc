/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class IntInPort extends NumericInPort implements INumericInPort {

  public IntInPort(String name) {
    super(name);
  }

  public IntInPort() {
    super();
  }

  int value;

  public int getValue() {
    return value;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  @Override
  public void update(byte _byte) {
    this.update((int) _byte);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  @Override
  public void update(short _short) {
    this.update((int) _short);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  @Override
  public void update(int _int) {
    this.value = _int;
    this.getObservers().forEach(p -> p.update(_int));
    this.synced = true;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  @Override
  public void update(long _long) {
    this.update((int) _long);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  @Override
  public void update(float _float) {
    this.update((int) _float);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  @Override
  public void update(double _double) {
    this.update((int) _double);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  @Override
  public void update(char _char) {
    this.update((int) _char);
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
