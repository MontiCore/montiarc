/* (c) https://github.com/MontiCore/monticore */
package dsim.reactions

import dsim.port.util.rangeTo
import openmodeautomata.runtime.Connector
import openmodeautomata.runtime.SourcePort
import openmodeautomata.runtime.TargetPort

typealias RteConnector = Connector
typealias SimConnector = dsim.port.util.Connector

/**
 * adds a decorator for connectors, as well as connector related methods
 */
abstract class AComponentWithConnectors(name: String) : AComponentForReactionsWithPorts(name) {

  override fun decorate(connector: SimConnector): RteConnector = ConnectorInReaction(connector)

  override fun connect(source: SourcePort, target: TargetPort) {
    if (!connectIfPossible(source, target)) {
      throw IllegalArgumentException("port ${target.name} is already connected")
    }
  }

  override fun connectIfPossible(source: SourcePort, target: TargetPort): Boolean {
    if (target.isConnected) {
      return false
    }
    connect((source as SourceInReaction).port..(target as TargetInReaction).port)
    return true
  }

  override fun connectAnyways(source: SourcePort, target: TargetPort): SourcePort? {
    val former = target.connector?.source
    target.deleteConnectors()
    connectIfPossible(source, target)
    return former
  }

  override fun autoconnect(name: Boolean) {
    val match = { s: SourcePort, t: TargetPort -> (!name || t.name == s.name) && t.fits(s) }
    // connect any unconnected ports
    unconnected(sourcePorts).forEach { source ->
      unconnected(targetPorts)
          .first { match(source, it) }
          .let { connect(source, it) }
    }
    // connect remaining targets to any sources, regardless of the source's connection status
    unconnected(targetPorts).forEach { target ->
      sourcePorts
          .first { match(it, target) }
          .let { connect(it, target) }
    }
  }

  /**
   * implements [RteConnector] by providing a decorator for [SimConnector]
   */
  inner class ConnectorInReaction(var connector: SimConnector) : RteConnector {
    override fun delete() {
      disconnect(connector)
    }

    override fun getSource() = decorate(connector.base)
    override fun getTarget() = decorate(connector.target)

    override fun setSource(newSource: SourcePort): SourcePort {
      val old = source
      delete()
      connector = (newSource as SourceInReaction).port..connector.target
      connect(connector)
      return old
    }

    override fun setTarget(newTarget: TargetPort): TargetPort {
      val old = target
      delete()
      connector = connector.base..(newTarget as TargetInReaction).port
      connect(connector)
      return old
    }

    override fun crossWith(other: RteConnector) {
      setSource(other.source.also { other.source = source })
      other.source = source.also { setSource(other.source) }
    }

    override fun getSourceComponent() = source.subcomponent
    override fun getTargetComponent() = target.subcomponent

    override fun isInput() = source.isInterface
    override fun isOutput() = target.isInterface
    override fun isHidden() = !isInput && !isOutput
  }

}