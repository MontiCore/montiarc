/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public interface ComponentType {

  /**
   * @return incoming ports of the interface of this component.
   */
  Collection<SourcePort> getInputPorts();

  /**
   * @return outgoing ports of the interface of this component.
   */
  Collection<TargetPort> getOutputPorts();

  /**
   * @return all output ports of subcomponents + {@link #getInputPorts()}
   */
  Collection<SourcePort> getSourcePorts();

  /**
   * @return all input ports of subcomponents + {@link #getOutputPorts()}
   */
  Collection<TargetPort> getTargetPorts();

  /**
   * @return the ports of this component and the ports of this type's subcomponents
   */
  Collection<PortElement> getAllPorts();

  /**
   * @return all entries of {@link #getAllPorts()} which are unconnected
   */
  Collection<PortElement> getUnconnectedPorts();

  /**
   * filters a list for unconnected ports
   *
   * @param ports a list with ports
   * @return the list, but without types
   */
  default <T extends PortElement> Collection<T> unconnected(@NotNull Collection<T> ports) {
    return ports.stream().filter(t -> !t.isConnected()).collect(Collectors.toList());
  }

  /**
   * @return all subcomponent instances of this component type
   */
  Collection<SubcomponentInstance> getSubcomponents();

  <T extends NamedArchitectureElement> T get(@NotNull String name, @NotNull Collection<T> list);

  /**
   * deactivates all subcomponents
   */
  void deactivateAll();

  /**
   * removes all subcomponents
   */
  void deleteAll();

  /**
   * removes all currently existing connectors
   */
  void disconnectAll();

  /**
   * connects all unconnected ports that can be connected
   *
   * @param name if true, only ports with matching names will be connected, if false only the ports type matters
   */
  void autoconnect(boolean name);

  /**
   * creates a connector between two ports
   *
   * @throws an exception, if the target port already is connected
   */
  void connect(@NotNull SourcePort source, @NotNull TargetPort target);

  /**
   * creates a connector between two ports. If the target is already connected, nothing will happen.
   *
   * @return whether this connector could be created
   */
  boolean connectIfPossible(@NotNull SourcePort source, @NotNull TargetPort target);

  /**
   * creates a connector between two ports. If the target is already connected, it will be disconnected at first.
   *
   * @return the source of the connector that was deleted, if there was one
   */
  SourcePort connectAnyways(@NotNull SourcePort source, @NotNull TargetPort target);
}
