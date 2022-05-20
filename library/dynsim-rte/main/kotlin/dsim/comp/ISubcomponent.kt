/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.IDataSink
import dsim.port.IDataSource

/**
 * Interface for Components to access their subcomponents.
 * Currently, this means accessing the in- and out-ports
 * as ConnectorTargets and ConnectorBases, respectively.
 */
interface ISubcomponent : ISubcomponentForTransition {

  override val name: String

  val inputPorts: Set<IDataSink>
  override val outputPorts: Set<IDataSource>

  fun getInputPort(name: String): IDataSink
  override fun getOutputPort(name: String): IDataSource

  suspend fun simulate()
  suspend operator fun invoke() = simulate()

  // suspend fun portChange(): Pair<IDataSource, PortChange>
}
