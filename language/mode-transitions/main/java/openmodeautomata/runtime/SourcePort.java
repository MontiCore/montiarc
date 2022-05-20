/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import java.util.Collection;

public interface SourcePort extends PortElement {

  /**
   * @return the connectors of the component that start at this port, or an empty list
   */
  Collection<Connector> getConnectors();

  @Override
  default boolean isTarget() {
    return false;
  }

  /**
   * created connectors to all ports, while leaving possible already existing connectors untouched
   *
   * @param targets ports to add
   */
  void connectAll(Collection<TargetPort> targets);

  /**
   * @return true if the types of both ports match
   */
  default boolean fits(TargetPort port) {
    return getType().equalsOrExtends(port.getType());
  }
}
