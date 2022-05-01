/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.conf.IReconfiguration
import dsim.log.log
import dsim.msg.IMessage
import dsim.msg.Tick
import dsim.port.IDataSink
import dsim.port.IDataSource
import dsim.port.IPort
import dsim.port.util.Connector
import dsim.port.util.rangeTo
import dsim.sched.util.ForwardEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

abstract class ADecomposedComponent(name: String) : AComponent(name), IReconfigurable {

  /**
   * Contains all current subcomponents of this component and their status.
   * It is a list, because when searching components by name, the ones added most recently should be found.
   * By default, cocos forbid duplicated names, but in reconfiguration actions cocos are less influential.
   */
  private val _subcomponents: MutableList<Entry<ISubcomponent>> = LinkedList() // linked list, because no random access needed, but frequent removing-operations
  override val subcomponents = object : Set<ISubcomponent> { // return a reference, not a copy to the original collection
    override val size = _subcomponents.size
    override fun contains(element: ISubcomponent) = _subcomponents.map {e -> e.get()}.contains(element)
    override fun containsAll(elements: Collection<ISubcomponent>) = _subcomponents.map {e -> e.get()}.containsAll(elements)
    override fun isEmpty() = _subcomponents.isEmpty()
    override fun iterator() = _subcomponents.map {e -> e.get()}.iterator()
  }

  /**
   * contains all connectors of this component.
   * This should maybe be implemented by something else than a set,
   * since remove-operations are frequent.
   */
  private val _connectors: MutableCollection<Entry<Connector>> = mutableSetOf()

  /**
   * marks ports as deactivated
   */
  private val _deactivatedPorts: MutableSet<IDataSink> = mutableSetOf()

  /**
   * marks ports as temporary
   */
  private val _temporaryPorts: MutableSet<IPort> = mutableSetOf()
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
    return _subcomponents.reversed().find { it.get().name == name }?.get() ?: throw NoSuchSubcomponentException()
  }

  /**
   * Forwards received message [msg] to all ports with a connector from [from] to them.
   */
  protected suspend fun forward(from: IDataSource, msg: IMessage) {
    _connectors.filter { it.activated }
            .map { it.get() }
            .filter { it.base == from }
            .filterNot { it.target in _deactivatedPorts }
            .forEach {
        try {
          if (msg !is Tick || it.target !in outputPorts) {
            it.target.pushMsg(msg)
          }
        } catch (e: ClosedSendChannelException) {
          disconnect(it)
          // todo: why remove - not add?
          _deactivatedPorts.remove(it.target)
        }
    }
  }

  protected suspend fun forward(event: ForwardEvent) {
    forward(event.port, event.msg)
  }

  /**
   * Makes the Component assume the Configuration described
   * by applying the change script to self.
   */
  protected fun reconfigure(config: IReconfiguration) {
    val changeScript = config.changeScript
    this.changeScript()
  }

  /**
   * removes all sub-elements that are marked as temporary
   */
  protected fun removeTemporaryElements(){
    _connectors.removeIf { !it.isPermanent() }
    _subcomponents.filter { !it.isPermanent() }.forEach{ delete(it.get()); }
    _temporaryPorts.forEach { removeInPort(it); removeOutPort(it) }
  }

  // Initialization methods===================================================

  /**
   * Adds a subcomponent before runtime
   */
  protected fun component(comp: ISubcomponent) {
    if (comp is IComponent) {
      _subcomponents.add(Entry(comp, permanent = true))
    }
  }

  // Reconfiguration methods==================================================

  // Subcomponents------------------------------------------------------------

  /**
   * Activates given component if it is currently deactivated
   * and is a true IComponent
   */
  final override fun activate(comp: ISubcomponent?) {
    // Only allow adding true IComponents
    if (comp is IComponent) {
      _subcomponents.find { e -> e.get() == comp}?.let { entry ->
        entry.activated = true
        comp.inputPorts.forEach { activatePort(it) }
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
    if (comp is IComponent) {
      _subcomponents.find { e -> e.get() == comp}?.let { entry ->
        entry.activated = false
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
    _subcomponents.filter { it.activated }.forEach { it.get().inputPorts.forEach { p -> deactivatePort(p) }}
    _subcomponents.forEach { it.activated = false }
  }

  /**
   * Permanently adds given subcomponent to subcomponents.
   * Also activates it.
   */
  final override fun create(comp: ISubcomponent, permanent: Boolean) {
    if (comp is IComponent) {
      _subcomponents.add(Entry(comp, permanent))
      // newSCs.send(comp)

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
      _subcomponents.removeIf { it.get() == comp }

      // Remove all connectors to [comp]
      comp.inputPorts.forEach { port ->
        _connectors.removeIf { it.get().target == port }
      }

      // Remove all connectors from [comp]
      comp.outputPorts.forEach { port ->
        _connectors.removeIf { it.get().base == port }
      }
    }

    // TODO: Cancel coroutine
  }

  /**
   * Deletes all subcomponents, active or not.
   */
  final override fun deleteAll() {
    _subcomponents.clear()

    // TODO: Cancel coroutines
  }

  // Connectors---------------------------------------------------------------

  /**
   * Add a connector from [base] to [target] if both exist
   */
  final override fun connect(base: IDataSource?, target: IDataSink?, permanent: Boolean) {
    if (target != null && base != null) {
      connect(base..target, permanent)
    }
  }

  /**
   * Remove connector from [base] to [target] if it exists.
   */
  final override fun disconnect(base: IDataSource?, target: IDataSink?) {
    if (target != null && base != null) {
      _connectors.removeIf { it.get().base == base && it.get().target == target }
    }
  }

  /**
   * Add given connector to this component
   */
  final override fun connect(conn: Connector, permanent: Boolean) {
    _connectors.add(Entry(conn, permanent))
  }

  /**
   * Remove given connector from this component
   */
  final override fun disconnect(conn: Connector) {
    _connectors.removeIf{it.get() == conn}
  }

  /**
   * Remove all connectors from this component
   */
  final override fun disconnectAll() {
    _connectors.clear()
  }

  // Ports---------------------------------------------------------------
  /**
   * creates a port that is not permanent, which means it will be deleted at the next mode-change
   */
  fun addTemporaryPort(incoming:Boolean=true, port:IPort){
    _temporaryPorts.add(port)
    if (incoming) {
      addInPort(port)
    } else {
      addOutPort(port)
    }
  }
}

/**
 * allows for storing arc-elements together with some attributes that describe their state in the dynamic world
 */
data class Entry<E:Any>(private val element: E, private val permanent:Boolean, var activated: Boolean = true){
  /**
   * the entry
   */
  fun get():E{
    return element
  }

  /**
   * permanent elements are not removed until explicitly requested to.
   * temporary elements are removed every mode-change
   */
  fun isPermanent():Boolean{
    return permanent
  }

  override fun hashCode(): Int {
    return get().hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other is Entry<*>){
      return get() == other.get()
    }
    return false
  }
}
