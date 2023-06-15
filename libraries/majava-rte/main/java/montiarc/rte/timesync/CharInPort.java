/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public class CharInPort extends NumericInPort implements INumericInPort {

  public CharInPort(String name) {
    super(name);
  }

  public CharInPort() {
    super();
  }

  char value;

  public char getValue() {
    return value;
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  @Override
  public void update(byte _byte) {
    this.update((char) _byte);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  @Override
  public void update(short _short) {
    this.update((char) _short);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  @Override
  public void update(int _int) {
    this.update((char) _int);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  @Override
  public void update(long _long) {
    this.update((char) _long);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  @Override
  public void update(float _float) {
    this.update((char) _float);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  @Override
  public void update(double _double) {
    this.update((char) _double);
  }

  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  @Override
  public void update(char _char) {
    this.value = _char;
    this.synced = true;
  }

  @Override
  protected void resetValue() {
    this.value = 0;
  }
}
