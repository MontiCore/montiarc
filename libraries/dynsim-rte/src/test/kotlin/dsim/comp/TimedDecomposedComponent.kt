/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.Port
import dsim.sched.decomposed.timedSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class TimedDecomposedComponent(name: String) : ADecomposedComponent(name) {

  init {
    addInPort(Port.make<String>("input 1"))
    addInPort(Port.make<String>("input 2"))

    addOutPort(Port.make<String>("output 1"))

    component(ExampleTimedAtomicComponent("sc"))

    connect(getInputPort("input 1"), getSubcomponent("sc").getInputPort("a"))
    connect(getInputPort("input 2"), getSubcomponent("sc").getInputPort("b"))

    connect(getSubcomponent("sc").getOutputPort("x"), getOutputPort("output 1"))
  }

  override suspend fun behavior() {
    timedSchedule(inputPorts, subcomponents).collect { event ->
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
