/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.log.log
import dsim.msg.Tick
import dsim.port.util.port
import dsim.sched.util.SingleMessageEvent
import dsim.sched.util.TickEvent
import dsim.sched.atomic.timedSchedule
import kotlinx.coroutines.flow.collect

class ExampleTimedAtomicComponent(name: String) : AComponent(
  name,
  inPorts = setOf(port<String>("a"), port<String>("b")),
  outPorts = setOf(port<String>("x"))
) {
  override suspend fun behavior() {
    timedSchedule(inputPorts).collect { event ->
      when (event) {
        is TickEvent -> {
          outputPorts.forEach { it.pushMsg(Tick()) }
        }
        is SingleMessageEvent -> {
          log("handling message ${event.msg}")
          getOutputPort("x").pushMsg(event.msg)
        }
      }
    }
  }
}
