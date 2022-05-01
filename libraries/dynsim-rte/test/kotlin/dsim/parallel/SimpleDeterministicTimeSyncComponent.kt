/* (c) https://github.com/MontiCore/monticore */
package dsim.parallel

import dsim.comp.AComponent
import dsim.comp.ADecomposedComponent
import dsim.msg.Message
import dsim.msg.Tick
import dsim.port.Port
import dsim.sched.decomposed.syncSchedule as dcSyncSchedule
import dsim.sched.atomic.syncSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.SyncEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class SimpleDeterministicTimeSyncComponent(name: String) : ADecomposedComponent(name) {
  init {
    addInPort(Port.make<String>("string 1"))
    addInPort(Port.make<String>("string 2"))
    addOutPort(Port.make<String>("output string"))

    component(StringMergerComponent("merger"))
    component(StringSorterComponent("sorter"))

    connect(getInputPort("string 1"), getSubcomponent("merger").getInputPort("string 1"))
    connect(getInputPort("string 2"), getSubcomponent("merger").getInputPort("string 2"))
    connect(getSubcomponent("merger").getOutputPort("output string"),
      getSubcomponent("sorter").getInputPort("input string"))
    connect(getSubcomponent("sorter").getOutputPort("output string"), getOutputPort("output string"))
  }

  override suspend fun behavior() {
    dcSyncSchedule(inputPorts, subcomponents).collect { event ->
      when (event) {
        is ForwardEvent -> {
          forward(event.port, event.msg)
        }
        is TickEvent -> {
          tickOutputs()
        }
      }
    }
  }
}

class StringMergerComponent(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<String>("string 1"))
    addInPort(Port.make<String>("string 2"))
    addOutPort(Port.make<String>("output string"))
  }

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is TickEvent -> {
          outputPorts.forEach { it.pushMsg(Tick()) }
        }
        is SyncEvent -> {
          val string1 = event.events[getInputPort("string 1")]?.payload as? String
          val string2 = event.events[getInputPort("string 2")]?.payload as? String

          if (string1 != null && string2 != null) {
            getOutputPort("output string").pushMsg(Message(string1 + string2))
          }
        }
      }
    }
  }
}

class StringSorterComponent(name: String) : AComponent(name) {
  init {
    addInPort(Port.make<String>("input string"))
    addOutPort(Port.make<String>("output string"))
  }

  override suspend fun behavior() {
    syncSchedule(inputPorts).collect { event ->
      when (event) {
        is TickEvent -> {
          outputPorts.forEach { it.pushMsg(Tick()) }
        }
        is SyncEvent -> {
          event.events[getInputPort("input string")]?.payload?.let {
            if (it is String) {
              val sorted = it.toSortedSet().toCharArray().concatToString()
              getOutputPort("output string").pushMsg(Message(sorted))
            }
          }
        }
      }
    }
  }
}
