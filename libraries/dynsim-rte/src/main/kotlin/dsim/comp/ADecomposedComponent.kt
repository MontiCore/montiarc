/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.conf.*
import dsim.log.log
import dsim.msg.IMessage
import dsim.msg.Tick
import dsim.port.IDataSink
import dsim.port.IDataSource
import dsim.port.util.Connector
import dsim.port.util.connector
import dsim.sched.util.ForwardEvent
import dsim.sched.util.IEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException

abstract class ADecomposedComponent(name: String) : AComponent(name), IReconfigurable {

  override val subcomponents: MutableSet<ISubcomponent> = mutableSetOf()

  private val _connectors: MutableSet<Connector> = mutableSetOf()

  //Helper attributes
  private var _currentReconfiguration: IReconfiguration? = null
  private val _deactivatedSCs: MutableSet<ISubcomponent> = mutableSetOf()
  private val _deactivatedPorts: MutableSet<IDataSink> = mutableSetOf()
  private val _temporarySCs: MutableSet<ISubcomponent> = mutableSetOf()
  // private val newSCs: Channel<IComponent> = Channel()

  private var _scope: CoroutineScope? = null

  override fun toString(): String = "component $name (decomposed)"


  init {
    log("component created")
  }

  // Starts simulation of the component's spf
  final override suspend fun simulate() = coroutineScope {
    _scope = this
    //launch { simulateSubcomponents(subcomponents) }
    subcomponents.forEach {
      launch { it() }
    }
    behavior()
    log("launched simulation")
  }

  // Public attribute getters=================================================

  /**
   * Gets parts from backing fields as implementation of interfaces.
   * Gets cast to abstraction given in the interface automatically.
   */

  final override fun getSubcomponent(name: String): ISubcomponent {
    return subcomponents.find { it.name == name } ?: throw NoSuchSubcomponentException()
  }

  /**
   * Forwards received message [msg] to all ports with a connector from [from] to them.
   */
  protected suspend fun forward(from: IDataSource, msg: IMessage) {
    _connectors.filter { it.base == from }.forEach {
      if (it.target !in _deactivatedPorts) {
        try {
          if (msg !is Tick || it.target !in outputPorts) {
            it.target.pushMsg(msg)
          }
        } catch (e: ClosedSendChannelException) {
          _connectors.remove(it)
          _deactivatedPorts.remove(it.target)
        }
      }
    }
  }

  protected suspend fun forward(event: ForwardEvent) {
    forward(event.port, event.msg)
  }

  protected suspend fun tickOutputs() {
    outputPorts.forEach { it.pushMsg(Tick()) }
  }

  /**
   * Makes the Component assume the Configuration described
   * by applying the change script to self.
   */
  protected fun reconfigure(config: IReconfiguration) {
    // Don't reconfigure if configuration already present
    if (_currentReconfiguration == config) return

    removeTemporarySCs()
    val changeScript = config.changeScript
    this.changeScript()

    _currentReconfiguration = config
  }

  /**
   * Removes all subcomponents from _subcomponents which are marked as temporary
   * by being in _temporarySCs.
   */
  private fun removeTemporarySCs() {
    _temporarySCs.forEach {
      delete(it)
    }
    _temporarySCs.clear()
  }

  // Initialization methods===================================================

  /**
   * Adds a subcomponent before runtime
   */
  protected fun component(comp: ISubcomponent) {
    if (comp is IComponent) {
      subcomponents.add(comp)
    }
  }

  // Reconfiguration methods==================================================

  // Subcomponents------------------------------------------------------------

  /**
   * Activates given component if it is currently deactivated
   * and is a true IComponent
   */
  final override fun activate(comp: ISubcomponent?) {
    comp?.let {
      if (it in _deactivatedSCs && it is IComponent) {
        // Only allow adding true IComponents
        _deactivatedSCs.remove(it)
        subcomponents.add(it)
        it.inputPorts.forEach { activatePort(it) }
      }
    }
  }

  private fun activatePort(port: IDataSink) {
    _deactivatedPorts.remove(port)
  }

  /**
   * Deactivates given component if currently activated
   * and is a true IComponent
   */
  final override fun deactivate(comp: ISubcomponent?) {
    comp?.let {
      if (comp in subcomponents && comp is IComponent) {
        subcomponents.remove(comp)
        _deactivatedSCs.add(comp)
        comp.inputPorts.forEach { deactivatePort(it) }
      }
    }
  }

  private fun deactivatePort(port: IDataSink) {
    _deactivatedPorts.add(port)
  }

  /**
   * Deactivates all currently active subcomponents.
   */
  final override fun deactivateAll() {
    subcomponents.forEach { comp ->
      subcomponents.remove(comp)
      _deactivatedSCs.add(comp)
      comp.inputPorts.forEach { deactivatePort(it) }
    }
  }

  /**
   * Permanently adds given subcomponent to subcomponents.
   * Also activates it.
   */
  final override fun create(comp: ISubcomponent) {
    if (comp is IComponent) {
      subcomponents.add(comp)
      // newSCs.send(comp)

      if (_scope?.isActive == true) {
        _scope?.launch {
          comp()
        }
      }
    }
  }

  final override fun temp(comp: ISubcomponent) {
    if (comp is IComponent) {
      subcomponents.add(comp)
      // newSCs.send(comp)
      _temporarySCs.add(comp)

      if (_scope?.isActive == true) {
        _scope?.launch {
          comp()
        }
      }
    }
  }

  /**
   * Permanently deletes given subcomponent from subcomponents.
   * Also works on currently deactivated subcomponents.
   */
  final override fun delete(comp: ISubcomponent) {
    if (comp is IComponent) {
      subcomponents.remove(comp)
      _deactivatedSCs.remove(comp)

      // Remove all connectors to [comp]
      comp.inputPorts.forEach { port ->
        _connectors.removeIf { it.target == port }
      }

      // Remove all connectors from [comp]
      comp.outputPorts.forEach { port ->
        _connectors.removeIf { it.base == port }
      }
    }

    // TODO: Cancel coroutine
  }

  /**
   * Deletes all subcomponents, active or not.
   */
  final override fun deleteAll() {
    subcomponents.clear()
    _deactivatedSCs.clear()

    // TODO: Cancel coroutines
  }

  // Connectors---------------------------------------------------------------

  /**
   * Add a connector from [base] to [target] if both exist
   */
  final override fun connect(base: IDataSource?, target: IDataSink?) {
    if (target != null && base != null) {
      connector(base, target).let { _connectors.add(it) }
    }
  }

  /**
   * Remove connector from [base] to [target] if it exists.
   */
  final override fun disconnect(base: IDataSource?, target: IDataSink?) {
    if (target != null && base != null) {
      _connectors.removeIf { it.base == base && it.target == target }
    }
  }

  /**
   * Add given connector to this component
   */
  final override fun connect(conn: Connector) {
    _connectors.add(conn)
  }

  /**
   * Remove given connector from this component
   */
  final override fun disconnect(conn: Connector) {
    _connectors.remove(conn)
  }

  /**
   * Remove all connectors from this component
   */
  final override fun disconnectAll() {
    _connectors.clear()
  }
}
