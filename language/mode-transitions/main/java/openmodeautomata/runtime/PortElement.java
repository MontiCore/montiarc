/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import java.util.Collection;

public interface PortElement extends NamedArchitectureElement {

  /**
   * never returns null, instead returns an empty list
   *
   * @return any connectors of the component, that are linked to this port
   */
  Collection<Connector> getConnectors();

  /**
   * @return the subcomponent that owns this port, or <code>null</code>, if this port belongs to the type's interface
   */
  SubcomponentInstance getSubcomponent();

  /**
   * @return true, if the component type has a connector which connects this port
   */
  default boolean isConnected() {
    return getConnectors().isEmpty();
  }

  /**
   * @return direction of the port
   */
  default boolean isIncoming() {
    return isSource() == isInterface();
  }

  /**
   * @return direction of the port
   */
  default boolean isOutgoing() {
    return isTarget() == isInterface();
  }

  /**
   * @return true if the port belongs to the component's interface, false if it belongs to a subcomponent
   */
  default boolean isInterface() {
    return getSubcomponent() == null;
  }

  /**
   * @return whether the port can be the source of a connector in this component
   */
  default boolean isSource() {
    return !isTarget();
  }

  /**
   * @return whether the port can be the target of a connector in this component
   */
  boolean isTarget();

  /**
   * @return the type of the messages sent through the port
   */
  DataType getType();

  /**
   * creates a connector between two ports.
   * The worst method for creating ports, since it provides no type check for the port direction
   *
   * @param port a port with a fitting direction
   * @throws an exception if port-types or directions do not match
   * @throws an exception if there already is a connector which prohibits creation of a new connector
   */
  void connectTo(PortElement port);

  /**
   * delete connectors of the component type, which are linked to this port
   *
   * @return true, if any connectors were deleted
   */
  boolean deleteConnectors();
}
