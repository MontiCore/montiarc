/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.timesync;

public interface IOutPort<T> extends IPort<T> {

  void setValue(T value);

  /**
   * Connect this to the observing input port
   *
   * @param port the observing input port
   */
  void connect(IInPort<T> port);

  /**
   * Sync this port with the observing input ports
   */
  void sync();
}
