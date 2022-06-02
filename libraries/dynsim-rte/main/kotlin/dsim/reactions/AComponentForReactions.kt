/* (c) https://github.com/MontiCore/monticore */
package dsim.reactions

import dsim.comp.ADecomposedComponent
import dsim.comp.ISubcomponent
import dsim.port.IDataSink
import dsim.port.IDataSource
import dsim.port.util.rangeTo
import openmodeautomata.runtime.*

/**
 * represents a decomposed component with the methods specifiedd by the openmodeautomataruntime
 */
abstract class AComponentForReactions(name: String) : ADecomposedComponent(name), ComponentType {

  override fun getInterfaceInputPorts(): List<SourcePort> = inputPorts.toCollection(ArrayList(inputPorts.size))

  override fun getInterfaceOutputPorts(): List<TargetPort> = outputPorts.toCollection(ArrayList(outputPorts.size))

  override fun getSourcePorts(): List<SourcePort> = interfaceInputPorts + subcomponents.flatMap { it.outputPorts }

  override fun getTargetPorts(): List<TargetPort> = interfaceOutputPorts + subcomponents.flatMap { it.inputPorts }

  override fun getAllPorts(): List<UndirectedPort> = sourcePorts + targetPorts

  override fun getSubcomponents(): List<SubcomponentInstance> = subcomponents.toCollection(ArrayList(subcomponents.size))

  override fun connectPorts(source: SourcePort?, target: TargetPort?) {
    if (!connectIfPossible(source, target)) {
      throw IllegalArgumentException("port $target is already connected")
    }
  }

  override fun connectIfPossible(source: SourcePort?, target: TargetPort?): Boolean {
    if (source==null){
      throw IllegalArgumentException("Given source port is null")
    }
    if (target==null){
      throw IllegalArgumentException("Given target port is null")
    }
    if (isConnected(target)) {
      return false
    }
    connect(source as IDataSource .. target as IDataSink)
    return true
  }

  override fun connectAnyways(source: SourcePort?, target: TargetPort?): SourcePort? {
    val former = getConnector(target)?.source
    deleteConnectors(target)
    connectIfPossible(source, target)

    return former
  }

  override fun activate(element: SubcomponentInstance?) = activate(element as? ISubcomponent?)
  override fun deactivate(element: SubcomponentInstance?) = deactivate(element as? ISubcomponent?)
  override fun isActive(element: SubcomponentInstance?) = (element as? ISubcomponent)?.let{isActive(it)}?:false
  override fun delete(element: SubcomponentInstance?) = (element as? ISubcomponent)?.let{delete(it)}?:Unit

  override fun getConnectors(port: UndirectedPort?): List<Connector> =
    _connectors.map { it.get() }.filter { it.base == port || it.target == port }

  override fun getSubcomponent(port: UndirectedPort?): SubcomponentInstance? =
      (port as? SourcePort)?.let { getSourceSubcomponent(it) }?:(port as? TargetPort)?.let { getTargetSubcomponent(it) }

  private fun getTargetSubcomponent(port: TargetPort): SubcomponentInstance? = if (outputPorts.contains(port)) {
    null
  } else {
    subcomponents.find { it.inputPorts.contains(port) }
  }

  private fun getSourceSubcomponent(port: SourcePort): SubcomponentInstance? = if (inputPorts.contains(port)) {
    null
  } else {
    subcomponents.find { it.outputPorts.contains(port) }
  }

  override fun isTarget(port: UndirectedPort?) = (getSubcomponent(port)?.inputPorts?:interfaceOutputPorts).contains(port)
  override fun deleteConnectors(port: UndirectedPort?) = getConnectors(port).forEach { delete(it) }
  override fun getConnector(port: TargetPort?): Connector? = getConnectors(port).getOrNull(0)
  override fun delete(connector: Connector?) = disconnect(connector as dsim.port.util.Connector)

  var discriminator = 0;
  fun generateName(name: String): String{
    while (true) {
      discriminator++
      if (subcomponents.none { it.name == (name+discriminator) }){
        return name+discriminator
      }
    }
  }
}