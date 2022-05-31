/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

/**
 * Output Ports of the enclosing component and incoming ports of subcomponents
 * may be connected as target-ports of connectors.
 * This interface furthermore provides methods for comparing types of ports.
 */
public interface TargetPort extends UndirectedPort {

  /**
   * @return true if the types of both ports match so that they can be connected.
   * True, if this is assignable from the other one
   */
  boolean matchesType(SourcePort other);

  /**
   * @return if type of the messages sent through the port exactly equals the type of the other port.
   * The ports' directions do not matter.
   */
  boolean equalsType(UndirectedPort other);

}