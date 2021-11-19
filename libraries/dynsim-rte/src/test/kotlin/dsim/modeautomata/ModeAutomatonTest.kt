/* (c) https://github.com/MontiCore/monticore */
package dsim.modeautomata

import dsim.comp.ISubcomponentForTransition
import dsim.msg.Message
import dsim.port.IDataSource
import dsim.port.util.port
import dsim.sched.util.SingleMessageEvent
import org.junit.Assert
import org.junit.Test

class ModeAutomatonTest {
  @Test
  fun `mode automata select modes according to transitions`() {
    val trigger = object : ITransitionTrigger {
      override val inputPorts: Set<IDataSource> = setOf(port<String>("input"))
      override val subcomponents: Set<ISubcomponentForTransition> = setOf()
      override val lastEvent = SingleMessageEvent(getInputPort("input"), Message("test message"))
    }

    val modeAutomaton = ModeAutomaton(trigger)

    // Initial mode
    modeAutomaton.mode(initial = true, "mode A") {  }

    // Next mode
    modeAutomaton.mode("mode B") {  }

    // Transition to mode B if test message is received
    modeAutomaton.transition("mode A", "mode B") { lastEvent.msg.payload == "test message" }

    // Test Code------------------------------------------------------------

    // Should start at mode A
    Assert.assertEquals("mode A", modeAutomaton.currentMode.name)

    modeAutomaton.update()

    // Should have changed to mode B
    Assert.assertEquals("mode B", modeAutomaton.currentMode.name)
  }
}
