/* (c) https://github.com/MontiCore/monticore */
package dsim.sim

import dsim.comp.ISimulatable
import dsim.comp.NoSuchPortException
import dsim.log.ILoggable
import dsim.msg.IMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking

interface ISimulation : ILoggable {
  fun start()
  fun stop()

  val inputs: Map<String, SendChannel<IMessage>>
  val outputs: Map<String, ReceiveChannel<IMessage>>

  fun input(name: String): SendChannel<IMessage> = inputs[name] ?: throw NoSuchPortException(name)
  fun output(name: String): ReceiveChannel<IMessage> = outputs[name] ?: throw NoSuchPortException(name)

  suspend fun tickInputs()
  suspend fun tickAtOutputs(): Boolean
}

/**
 * Function with receiver.
 * Builds a simulation of [comp] using the scope from the receiver [this] and returns it.
 * @receiver the coroutine scope the simulation coroutine inherits.
 * @param comp the component to be simulated
 * @return the Simulation object
 */
fun CoroutineScope.backgroundSimulation(comp: ISimulatable): ISimulation = Simulation(comp, this)

inline fun runSimulation(comp: ISimulatable, crossinline testScript: suspend ISimulation.() -> Unit): ISimulation {
  return runBlocking {
    val sim = Simulation(comp, this)
    sim.testScript()

    sim.stop()
    return@runBlocking sim
  }
}