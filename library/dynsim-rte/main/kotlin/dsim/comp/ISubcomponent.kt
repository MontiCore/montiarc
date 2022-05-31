/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.IDataSink
import dsim.port.IDataSource
import openmodeautomata.runtime.SourcePort
import openmodeautomata.runtime.SubcomponentInstance
import openmodeautomata.runtime.TargetPort
import openmodeautomata.runtime.UndirectedPort

/**
 * Interface for Components to access their subcomponents.
 * Currently, this means accessing the in- and out-ports
 * as ConnectorTargets and ConnectorBases, respectively.
 */
interface ISubcomponent : ISubcomponentForTransition, SubcomponentInstance {

  override val name: String

  val inputPorts: Set<IDataSink>
  override val outputPorts: Set<IDataSource>

  override fun getInputPort(name: String): IDataSink
  override fun getOutputPort(name: String): IDataSource

  suspend fun simulate()
  suspend operator fun invoke() = simulate()

  // suspend fun portChange(): Pair<IDataSource, PortChange>

  override fun getPorts(): List<UndirectedPort> = getInputPorts() + getOutputPorts()
  override fun getInputPorts(): List<TargetPort> = inputPorts.toCollection(ArrayList(inputPorts.size))
  override fun getOutputPorts(): List<SourcePort> = outputPorts.toCollection(ArrayList(outputPorts.size))
}
