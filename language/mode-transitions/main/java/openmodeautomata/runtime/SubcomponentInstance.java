/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import java.util.Collection;

public interface SubcomponentInstance extends NamedArchitectureElement {

  /**
   * @return all ports of this subcomponent
   */
  Collection<PortElement> getPorts();

  /**
   * @return all incoming ports of this subcomponent
   */
  Collection<PortElement> getInputPorts();

  /**
   * @return all outgoing ports of this subcomponent
   */
  Collection<PortElement> getOutputPorts();

  /**
   * deactivates this element (and all related connectors)
   */
  void deactivate();

  /**
   * reactivates this element
   */
  void activate();

  /**
   * @return false, if the element was explicitly deactivated
   */
  boolean isActive();

  /**
   * deletes this element (and all related connectors)
   */
  void delete();


}
