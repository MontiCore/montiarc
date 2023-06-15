/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface INumericInPort extends IPrimitivePort {
  
  /**
   * @return whether this was already synchronized with the observed output
   * port in the current time slice
   */
  boolean isSynced();
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _byte the msg sent via the observed output port
   */
  void update(byte _byte);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _short the msg sent via the observed output port
   */
  void update(short _short);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _int the msg sent via the observed output port
   */
  void update(int _int);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _long the msg sent via the observed output port
   */
  void update(long _long);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _float the msg sent via the observed output port
   */
  void update(float _float);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _double the msg sent via the observed output port
   */
  void update(double _double);
  
  /**
   * Synchronizes this with the observed port with the given msg
   *
   * @param _char the msg sent via the observed output port
   */
  void update(char _char);
}
