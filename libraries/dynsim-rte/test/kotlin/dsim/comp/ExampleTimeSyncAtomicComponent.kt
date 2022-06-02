/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.util.port
import dsim.sched.atomic.syncSchedule
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class ExampleTimeSyncAtomicComponent(name: String) : AComponent(name,
    inPorts = setOf(port<String>("a"), port<Int>("b")),
    outPorts = setOf(port<String>("x"))) {

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect {
      when (it) {
        is SyncEvent -> {
          val a = it[getInputPort("a")]?.payload
          val b = it[getInputPort("b")]?.payload

          getOutputPort("x").pushMsg(Message("$a$b"))

        }
        is TickEvent -> {
          getOutputPort("x").pushMsg(Tick())
        }
      }
    }
  }
}