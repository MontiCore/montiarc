/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A symbol containing all methods which should be usable in mode automaton reactions
 */
public interface ComponentType {
  /**
   * @return incoming ports of the interface of this component.
   */
  List<SourcePort> getInterfaceInputPorts();

  /**
   * filters {@link #getInterfaceInputPorts()} for the port with the specific name
   * @return null, if the port does not exist
   */
  SourcePort getInputPort(String name);

  /**
   * @return outgoing ports of the interface of this component.
   */
  List<TargetPort> getInterfaceOutputPorts();

  /**
   * filters {@link #getInterfaceOutputPorts()} for the port with the specific name
   * @return null, if the port does not exist
   */
  TargetPort getOutputPort(String name);

  /**
   * @return all output ports of subcomponents + {@link #getInterfaceInputPorts()}
   */
  List<SourcePort> getSourcePorts();

  /**
   * @return all input ports of subcomponents + {@link #getInterfaceOutputPorts()}
   */
  List<TargetPort> getTargetPorts();

  /**
   * @return the ports of this component and the ports of this type's subcomponents
   */
  List<UndirectedPort> getAllPorts();

  /**
   * @return all subcomponent instances of this component type
   */
  Collection<SubcomponentInstance> getSubcomponents();

  /**
   * filters {@link #getSubcomponents()} for the element with the specific name
   * @return null, if the instance does not exist
   */
  SubcomponentInstance getSubcomponent(String name);

  /**
   * deactivates all subcomponents (and their connectors)
   */
  void deactivateAll();

  /**
   * removes all subcomponents (and their connectors)
   */
  void deleteAll();

  /**
   * removes all currently existing connectors
   */
  void disconnectAll();

  /**
   * creates a connector between two ports
   */
  void connectPorts(SourcePort source, TargetPort target);

  /**
   * creates a connector between two ports. If the target is already connected, nothing will happen.
   *
   * @return whether this connector could be created
   */
  boolean connectIfPossible(SourcePort source, TargetPort target);

  /**
   * creates a connector between two ports. If the target is already connected, it will be disconnected at first.
   *
   * @return the source of the connector that was deleted, if there was one
   */
  SourcePort connectAnyways(SourcePort source, TargetPort target);

  /**
   * deactivates this element (and all related connectors)
   */
  void deactivate(SubcomponentInstance element);

  /**
   * reactivates this element
   */
  void activate(SubcomponentInstance element);

  /**
   * @return false, if the element was explicitly deactivated
   */
  boolean isActive(SubcomponentInstance element);

  /**
   * deletes this element (and all related connectors)
   */
  void delete(SubcomponentInstance element);

  /**
   * @return any connectors of the component, that are linked to this port.
   * Never returns null, instead returns an empty list.
   * TargetPorts will never return Collections with more than one Entry, SourcePorts, however, might do.
   */
  List<Connector> getConnectors(UndirectedPort port);

  /**
   * @return the subcomponent that owns this port, or <code>null</code>, if this port belongs to the type's interface
   */
  SubcomponentInstance getSubcomponent(UndirectedPort port);

  /**
   * @return true, if the component type has a connector which connects this port
   */
  default boolean isConnected(UndirectedPort port){
    return !getConnectors(port).isEmpty();
  }

  /**
   * @return direction of the port
   * return isSource() == isInterface();
   */
  default boolean isIncoming(UndirectedPort port){
    return isSource(port) == isInterface(port);
  }

  /**
   * @return direction of the port
   * return isTarget() == isInterface();
   */
  default boolean isOutgoing(UndirectedPort port){
    return isTarget(port) == isInterface(port);
  }

  /**
   * @return true if the port belongs to the component's interface, false if it belongs to a subcomponent
   * return getSubcomponent() == null;
   */
  default boolean isInterface(UndirectedPort port){
    return getSubcomponent(port) == null;
  }

  /**
   * @return whether the port can be the source of a connector in this component
   */
  default boolean isSource(UndirectedPort port){
    return !isTarget(port);
  }

  /**
   * @return whether the port can be the target of a connector in this component
   */
  boolean isTarget(UndirectedPort port);

  /**
   * delete connectors of the component type, which are linked to this port
   */
  void deleteConnectors(UndirectedPort port);

  /**
   * @return the connector that ends in this port, if there is one in this component type
   */
  Connector getConnector(TargetPort port);

  /**
   * source component of the connector, null if it is this component
   */
  default SubcomponentInstance getSourceComponent(Connector connector){
    return getSubcomponent(connector.getSource());
  }

  /**
   * source component of the connector, null if it is this component
   */
  default SubcomponentInstance getTargetComponent(Connector connector){
    return getSubcomponent(connector.getTarget());
  }

  /**
   * disconnects the ports that have been connected by the given connector
   */
  void delete(Connector connector);

  /**
   * Disassembles both connectors and reconnects them in a new way.
   * Switches the start of both connectors, while leaving their ends untouched.
   * Example:
   * a -> c;
   * b -> d;
   * will result in
   * a -> d;
   * b -> c;
   */
  default void cross(Connector first, Connector second){
    SourcePort a = first.getSource();
    SourcePort b = second.getSource();
    TargetPort c = first.getTarget();
    TargetPort d = second.getTarget();
    delete(first);
    delete(second);
    connectPorts(a, d);
    connectPorts(b, c);
  }
}