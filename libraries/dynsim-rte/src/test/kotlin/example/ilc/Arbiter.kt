/* (c) https://github.com/MontiCore/monticore */
package example.ilc

import dsim.comp.AComponent
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.Port
import dsim.sched.atomic.syncSchedule
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import example.ilc.signals.LightCmd
import example.ilc.signals.LightRequest
import kotlinx.coroutines.flow.collect

class Arbiter(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<LightRequest>("request"))

    addOutPort(Port.make<LightCmd>("lightCmd"))
  }

  var lastCmd = LightCmd.OFF

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is SyncEvent -> {
          event[getInputPort("request")]?.let {
            if (it.payload == LightRequest.OFF && lastCmd == LightCmd.ON) {
              getOutputPort("lightCmd").pushMsg(Message(LightCmd.OFF))
              lastCmd = LightCmd.OFF
            } else if (it.payload == LightRequest.ON && lastCmd == LightCmd.OFF) {
              getOutputPort("lightCmd").pushMsg(Message(LightCmd.ON))
              lastCmd = LightCmd.ON
            }
          }
        }
        is TickEvent -> {
          getOutputPort("lightCmd").pushMsg(Tick())
        }
      }
    }
  }
}
