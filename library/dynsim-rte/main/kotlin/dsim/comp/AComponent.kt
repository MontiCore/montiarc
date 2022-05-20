/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.log.log
import dsim.msg.Tick
import dsim.port.IDataSink
import dsim.port.IDataSource
import dsim.port.IPort
import kotlinx.coroutines.coroutineScope

/**
 * Implementations of atomic Components should inherit from this
 */
abstract class AComponent(
    final override val name: String, inPorts: Set<IPort> = setOf(),
    outPorts: Set<IPort> = setOf(),
) : IComponent {

  init {
    log("component created")
  }

  override fun toString() = "component $name (atomic)"

  /*
   * Port sets of atomic components can be mutable.
   * There is currently no way for implementations to mutate their ports.
   */
  override val inputPorts: MutableSet<IPort> = inPorts.toMutableSet()
  override val outputPorts: MutableSet<IPort> = outPorts.toMutableSet()

  // Starts simulation of the component's spf
  override suspend fun simulate() = coroutineScope {
    //launch { behaviour() }
    behavior()
    log("launched simulation")
  }

  /**
   * starts a scheduler that responds to and creates port messages
   * depending on the selected timing, it may be necessary to provide initial values for outgoing ports
   */
  protected abstract suspend fun behavior()

  override fun getInputPort(name: String): IPort {
    return this.inputPorts.find { it.name == name }
        ?: throw NoSuchPortException(name)
  }

  override fun getOutputPort(name: String): IPort {
    return this.outputPorts.find { it.name == name }
        ?: throw NoSuchPortException(name)
  }

  // Reconfiguration Operations===============================================

  // Mutating Ports
  override fun addInPort(port: IPort) {
    inputPorts.find { it.name == port.name }?.let {
      if (it.type != port.type) throw NameExistsException(it.name)
      return
    }

    inputPorts.add(port)
    log("added port $port as input")
  }

  override fun removeInPort(port: IDataSource) {
    inputPorts.remove(port)
    port.close()
    log("removed port $port as input")
  }

  @Suppress("EXPERIMENTAL_API_USAGE")
  override fun addOutPort(port: IPort) {
    inputPorts.find { it.name == port.name }?.let {
      if (it.receiveChannel.isClosedForReceive) {
        inputPorts.remove(it)
      } else {
        if (it.type != port.type) throw NameExistsException(it.name)
        return
      }
    }
    outputPorts.add(port)
    log("added port $port as output")
  }

  override fun removeOutPort(port: IDataSink) {
    // outputPorts.remove(port)
    port.close()
    log("removed port $port as output")
  }

  protected suspend fun tickOutputs() {
    outputPorts.forEach { it.pushMsg(Tick()) }
  }
}
