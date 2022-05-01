/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Tick
import dsim.port.util.port
import dsim.sched.atomic.timedSchedule
import dsim.sched.util.SingleMessageEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class FunctionalScheduledTimedAtomicComponent(name: String) : AComponent(name,
    setOf(port<String>("1"), port<String>("2")), setOf(port<String>("out"))) {

  override suspend fun behavior() {
    timedSchedule(inputPorts).collect { event ->
      when (event) {
        is SingleMessageEvent -> getOutputPort("out").pushMsg(event.msg)
        is TickEvent -> getOutputPort("out").pushMsg(Tick())
      }
    }
  }
}
