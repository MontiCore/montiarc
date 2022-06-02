/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.runtime;

import basicmodeautomata._symboltable.IBasicModeAutomataScope;

import java.util.*;

/**
 * In mode transitions one may query and manipulate subcomponents using this interface and {@link ComponentType}.
 * In those reactions, new subcomponents can also be created using constructors as described by {@link openmodeautomata._symboltable.OpenModeAutomataScopesGenitor#addConstructor(IBasicModeAutomataScope) constructor}
 */
public interface SubcomponentInstance {
  /**
   * @return all ports of this subcomponent
   */
  List<UndirectedPort> getPorts();

  /**
   * @return all incoming ports of this subcomponent
   */
  List<TargetPort> getInputPorts();
  TargetPort getInputPort(String name);

  /**
   * @return all outgoing ports of this subcomponent
   */
  List<SourcePort> getOutputPorts();
  SourcePort getOutputPort(String name);

}