/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

/**
 * in mode transitions one may query and manipulate connectors using this interface and {@link ComponentType}
 */
public interface Connector {

  /**
   * @return the connected port
   */
  SourcePort getSource();

  /**
   * @return the connected port
   */
  TargetPort getTarget();
}