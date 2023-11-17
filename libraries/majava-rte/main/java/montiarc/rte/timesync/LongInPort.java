/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class LongInPort extends NumericInPort implements INumericInPort {

  public LongInPort(String name) {
    super(name);
  }

  public LongInPort() {
    super();
  }

  long value;

  public long getValue() {
    return value;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  @Override
  public void update(byte _byte) {
    this.update((long) _byte);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  @Override
  public void update(short _short) {
    this.update((long) _short);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  @Override
  public void update(int _int) {
    this.update((long) _int);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  @Override
  public void update(long _long) {
    this.value = _long;
    this.getObservers().forEach(p -> p.update(_long));
    this.synced = true;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  @Override
  public void update(float _float) {
    this.update((long) _float);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  @Override
  public void update(double _double) {
    this.update((long) _double);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  @Override
  public void update(char _char) {
    this.update((long) _char);
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
