/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.modeautomata.ITransitionTrigger
import dsim.modeautomata.ModeAutomaton
import dsim.port.util.port
import dsim.sched.util.SingleMessageEvent
import dsim.sched.decomposed.untimedSchedule
import dsim.sched.util.ForwardEvent
import dsim.sched.util.TickEvent
import kotlinx.coroutines.flow.collect

class ExampleModeAutomatonComponent(name: String) : ADecomposedComponent(name) {
  private val transitionTrigger = object : ITransitionTrigger {
    override val inputPorts get() = this@ExampleModeAutomatonComponent.inputPorts
    override val subcomponents get() = this@ExampleModeAutomatonComponent.subcomponents

    override var lastEvent: SingleMessageEvent? = null
  }

  private val modeAutomaton = ModeAutomaton(transitionTrigger)

  init {
    addInPort(port<String>("input"))
    addInPort(port<String>("switch"))
    addOutPort(port<String>("output1"))
    addOutPort(port<String>("output2"))

    modeAutomaton.mode(initial = true, "mode A") {
      disconnectAll()
      connect(getInputPort("input"), getOutputPort("output1"))
    }
    modeAutomaton.mode("mode B") {
      disconnectAll()
      connect(getInputPort("input"), getOutputPort("output2"))
    }

    modeAutomaton.transition("mode B", "mode A") {
      lastEvent?.port == getInputPort("switch") &&
          lastEvent?.msg?.payload == "switch to A"
    }

    modeAutomaton.transition("mode A", "mode B") {
      lastEvent?.port == getInputPort("switch") &&
          lastEvent?.msg?.payload == "switch to B"
    }
  }

  /**
   * Transition trigger is updated with new event and potential reconfiguration
   * takes place BEFORE message is forwarded.
   */
  override suspend fun behavior() {
    untimedSchedule(inputPorts).collect { event ->
      when (event) {
        is SingleMessageEvent -> {
          transitionTrigger.lastEvent = event
          reconfigure(modeAutomaton.update())
        }
        is TickEvent -> {
          tickOutputs()
        }
        is ForwardEvent -> {
          forward(event.port, event.msg)
        }
      }
    }
  }
}
