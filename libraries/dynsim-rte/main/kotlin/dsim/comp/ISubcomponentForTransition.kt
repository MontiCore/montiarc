/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.IDataSource

/**
 * In the context of a transitionCondition, only
 * outputPorts of subcomponents are relevant data.
 */
interface ISubcomponentForTransition {

  val name: String
  val outputPorts: Set<IDataSource>
  fun getOutputPort(name: String) = outputPorts.find { it.name == name } ?: throw NoSuchPortException(name)
}
