/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.TickedMessage;
import sim.sched.IScheduler;
import sim.comp.ISimComponent;

import java.util.Queue;

/**
 * Simulation specific methods of an incoming port.
 *
 * @param <T> data type of this port
 */
public interface IInSimPort<T> extends IInPort<T> {
  /**
   * @return the component that owns this port
   */
  ISimComponent getComponent();

  /**
   * @return true, if last buffered message is a tick.
   */
  boolean hasTickReceived();

  /**
   * Checks, if this port contains unsent messages.
   *
   * @return true, if this ports has messages in its buffer.
   */
  boolean hasUnprocessedMessages();

  /**
   * @return true, if this port is connected, else false.
   */
  boolean isConnected();

  /**
   *
   */
  void processBufferedMsgs();

  /**
   * Sets the port to a connected state.
   */
  void setConnected();

  /**
   * Sets component and scheduler of this incoming port and signs up this port on the scheduler.
   *
   * @param component the component that owns this port
   * @param scheduler the scheduler that handles this port
   */
  void setup(ISimComponent component, IScheduler scheduler);

  void wakeUp();

  /**
   * Used by the scheduler to set the port number from this port to the given number nr.
   *
   * @param nr port number to set
   */
  void setPortNumber(int nr);

  /**
   * @return the port number from this port
   */
  int getPortNumber();


  void processMessageQueue();

  Queue<TickedMessage<?>> getMessageQueue();

  void setMessageQueue(Queue<TickedMessage<?>> messageQueue);
}
