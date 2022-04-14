/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.generic.ISimComponent;

import java.util.Collection;

/**
 * Simulation specific methods of an outgoing port. *
 *
 * @param <T> data type of this port
 */
public interface IOutSimPort<T> extends IOutPort<T> {
  /**
   * Adds the given receiver to the receivers of this port.
   *
   * @param receiver receiver to add
   */
  void addReceiver(IInPort<? super T> receiver);

  /**
   * @return the component that owns this port
   */
  ISimComponent getComponent();

  /**
   * @return a set of receivers of this port
   */
  Collection<IInPort<? super T>> getReceivers();

  /**
   * Sets the component of this port.
   *
   * @param component the component that owns this port
   */
  void setComponent(ISimComponent component);
}
