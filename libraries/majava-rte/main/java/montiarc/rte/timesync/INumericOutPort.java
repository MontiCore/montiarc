/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface INumericOutPort extends IPrimitivePort {
  
  /**
   * Connect this to the observing input port
   *
   * @param port the observing input port
   */
  void connect(INumericInPort port);
  
  /**
   * Sync this port with the observing input ports
   */
  void sync();
}
