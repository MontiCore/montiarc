/* (c) https://github.com/MontiCore/monticore */
package dsim.reactions

import dsim.comp.ADecomposedComponent
import dsim.comp.ISubcomponent
import dsim.port.IDataSink
import dsim.port.IDataSource
import openmodeautomata.runtime.*

/**
 * adds decorators for ports
 */
abstract class AComponentForReactionsWithPorts(name: String) : ADecomposedComponent(name), ComponentType {

  fun decorate(port: IDataSource) = SourceInReaction(port)
  fun decorate(port: IDataSink) = TargetInReaction(port)
  abstract fun decorate(subcomponent: ISubcomponent): SubcomponentInstance
  abstract fun decorate(connector: SimConnector): RteConnector

  /**
   * symbolizes a port, decorates [dsim.port.IPort] to implement [PortElement]
   */
  abstract inner class PortInReaction(open val port: Any) : PortElement {
    abstract val owner: ISubcomponent?
    override fun isInterface() = owner == this@AComponentForReactionsWithPorts
    override fun getName() = port.toSink()?.name
        ?: port.toSource()!!.name

    override fun getType(): DataType = TypeInReaction(port.portType)

    override fun getSubcomponent(): SubcomponentInstance? {
      return owner.takeIf { it != this@AComponentForReactionsWithPorts }?.let { decorate(it) }
    }

    override fun deleteConnectors(): Boolean {
      return _connectors.map { it.get() }
          .filter {
            (isSource.thenTake(it.base)
                ?: it.target) == port
          }
          .onEach { disconnect(it) }
          .count() != 0
    }

    override fun connectTo(port: PortElement) {
      if (this.isSource == port.isSource) {
        throw RuntimeException("Port direction missmatch")
      }
      val ports = listOf(this, port)
      connect(ports.filterIsInstance<SourcePort>()[0], ports.filterIsInstance<TargetPort>()[0])
    }
  }

  /**
   * symbolizes a port that can be the source of a connector, decorates [dsim.port.IPort] to implement [SourcePort]
   */
  inner class SourceInReaction(override val port: IDataSource) : PortInReaction(port.type), SourcePort {
    override val owner: ISubcomponent?
      get() = if (this@AComponentForReactionsWithPorts.inputPorts.contains(port)) {
        this@AComponentForReactionsWithPorts
      } else {
        subcomponents.find { it.outputPorts.contains(port) }
      }

    override fun getConnectors() =
        _connectors.filter { it.get().base == port }.map { decorate(it.get()) }

    override fun connectAll(targets: MutableCollection<TargetPort>) = targets.forEach { connect(this, it) }
  }

  /**
   * symbolizes a port that can be the target of a connector, decorates [dsim.port.IPort] to implement [TargetPort]
   */
  inner class TargetInReaction(override val port: IDataSink) : PortInReaction(port.type), TargetPort {
    override val owner: ISubcomponent?
      get() = if (this@AComponentForReactionsWithPorts.outputPorts.contains(port)) {
        this@AComponentForReactionsWithPorts
      } else {
        subcomponents.find { it.inputPorts.contains(port) }
      }

    override fun getConnector(): RteConnector? =
        _connectors.find { it.get().target == port }?.let { decorate(it.get()) }

    override fun rerouteFrom(port: SourcePort): SourcePort? = connectAnyways(port, this)
  }

}