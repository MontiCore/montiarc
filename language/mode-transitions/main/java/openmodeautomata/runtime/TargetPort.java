/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import java.util.Collection;
import java.util.Collections;

public interface TargetPort extends PortElement {

  /**
   * @return the connector that ends in this port, if there is one in this component type
   */
  Connector getConnector();

  @Override
  default boolean isTarget() {
    return true;
  }

  default Collection<Connector> getConnectors() {
    Connector input = getConnector();
    return input == null ? Collections.emptyList() : Collections.singleton(input);
  }

  /**
   * creates a new connector between this and the given port. If there already is a connection, it will be deleted
   *
   * @param port port to connect to
   * @return the port that was previously connected to this port, if there was one
   */
  SourcePort rerouteFrom(SourcePort port);

  /**
   * @return true if the types of both ports match
   */
  default boolean fits(SourcePort port) {
    return port.fits(this);
  }
}
