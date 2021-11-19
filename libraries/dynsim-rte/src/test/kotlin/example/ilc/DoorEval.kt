/* (c) https://github.com/MontiCore/monticore */
package example.ilc

import dsim.comp.AComponent
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.Port
import dsim.sched.atomic.syncSchedule
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import example.ilc.signals.DoorStatus
import example.ilc.signals.LightRequest
import kotlinx.coroutines.flow.collect

class DoorEval(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<DoorStatus>("status"))
    addOutPort(Port.make<LightRequest>("request"))
  }

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is SyncEvent -> {
          event[getInputPort("status")]?.let {
            if (it.payload == DoorStatus.OPEN) getOutputPort("request").pushMsg(Message(LightRequest.ON))
            else getOutputPort("request").pushMsg(Message(LightRequest.OFF))
          }
        }
        is TickEvent -> {
          getOutputPort("request").pushMsg(Tick())
        }
      }
    }
  }
}
