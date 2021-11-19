/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.port.util.port
import dsim.sched.decomposed.untimedSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class ExampleStaticDecomposedComponent(name: String) : ADecomposedComponent(name) {
  init {
    addInPort(port<String>("in"))
    addInPort(port<String>("alt"))

    addOutPort(port<String>("out"))

    component(ExampleAtomicComponent("exSc"))

    connect(getInputPort("in"), getSubcomponent("exSc").getInputPort("oof"))
    connect(getInputPort("alt"), getSubcomponent("exSc").getInputPort("oof"))

    connect(getSubcomponent("exSc").getOutputPort("foo"), getOutputPort("out"))
  }

  override suspend fun behavior() {
    untimedSchedule(inputPorts, subcomponents).collect { event ->
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
