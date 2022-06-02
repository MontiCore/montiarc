/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.comp.ISubcomponentForTransition
import dsim.comp.NoSuchPortException
import dsim.comp.NoSuchSubcomponentException
import dsim.port.IDataSource
import dsim.sched.util.IEvent

/**
 * Interface to be used by Transformation Conditions.
 * Methods implementing this interface should not change the internal
 * state of the class, i.e. be side-effect-free.
 */
interface IGuardInterface {

  /**
   * Gets own subcomponents as Subcomponents for Transition,
   * which can only provide the output Ports as DataSources
   */
  val subcomponents: Set<ISubcomponentForTransition>
  fun getSubcomponent(name: String) = subcomponents.find { it.name == name } ?: throw NoSuchSubcomponentException()

  /** Gets own input ports as Data Sources */
  val inputPorts: Set<IDataSource>
  fun getInputPort(name: String) = inputPorts.find { it.name == name } ?: throw NoSuchPortException(name)

  /**
   * the most recent event that happened and can now be used in transition-guards
   */
  val lastEvent: IEvent?
}
