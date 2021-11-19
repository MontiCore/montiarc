/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.log.ILoggable
import dsim.port.IPort

interface IComponent : ISubcomponent, ISubcomponentForTransition, IInterfaceReconfigurable, ISimulatable, ILoggable {

  override val name: String

  override suspend fun simulate()
  override suspend operator fun invoke() = simulate()

  // bare metal getters for actual Components and Port interfaces
  override val inputPorts: Set<IPort>
  override val outputPorts: Set<IPort>

  override fun getInputPort(name: String): IPort
  override fun getOutputPort(name: String): IPort
}
