/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.IPort

interface ISimulatable {

  // Starts the spf of this component in the given scope.
  suspend fun simulate()
  suspend operator fun invoke() = simulate()

  val inputPorts: Set<IPort>
  val outputPorts: Set<IPort>
}
