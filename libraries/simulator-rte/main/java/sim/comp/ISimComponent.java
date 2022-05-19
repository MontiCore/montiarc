/* (c) https://github.com/MontiCore/monticore */
package sim.comp;

import sim.message.Message;
import sim.port.IInPort;
import sim.serialiser.BackTrackHandler;

/**
 * Simulation specific methods of a component.
 */
public interface ISimComponent extends IComponent {
  /**
   * Is called by the scheduler.
   *
   * @param port    the port that received the message
   * @param message the message that is received by the port
   */
  void handleMessage(IInPort<?> port, Message<?> message);

  /**
   * Sets the name from this component with the given name.
   *
   * @param componentName name to set
   */
  void setComponentName(String componentName);

  /**
   * Stimulates the component to emit a tick on all outgoing ports and to increment the local time.
   */
  void handleTick();

  /**
   * Used by the scheduler to set the simulation id.
   *
   * @param id simulation id to set.
   */
  void setSimulationId(int id);

  /**
   * @return the simulation id from this component.
   */
  int getSimulationId();

  void setBth(BackTrackHandler bth);

  BackTrackHandler getBth();
}
