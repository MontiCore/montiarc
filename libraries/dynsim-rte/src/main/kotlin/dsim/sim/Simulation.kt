/* (c) https://github.com/MontiCore/monticore */
package dsim.sim

import dsim.comp.ISimulatable
import dsim.log.log
import dsim.msg.IMessage
import dsim.msg.Tick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class Simulation(private val comp: ISimulatable, private val scope: CoroutineScope) : ISimulation {
  private var simulation = scope.launch {
    launch { comp() }
  }

  override val inputs: Map<String, SendChannel<IMessage>>
    get() = comp.inputPorts.associate { Pair(it.name, it.sendChannel) }

  override val outputs: Map<String, ReceiveChannel<IMessage>>
    get() = comp.outputPorts.associate { Pair(it.name, it.receiveChannel) }

  override fun start() {
    if (!simulation.isActive) {
      simulation = scope.launch {
        launch { comp() }
      }

      log("simulation started")
    } else {
      log("simulation already running")
    }
  }

  override fun stop() {
    simulation.cancel()
    log("simulation stopped")
  }

  override suspend fun tickInputs() = inputs.forEach { (_, port) -> port.send(Tick()) }
  override suspend fun tickAtOutputs(): Boolean {
    outputs.forEach { (_, port) ->
      if (port.receive() !is Tick) return false
    }
    return true
  }

  override fun toString(): String = "simulation of $comp"
}
