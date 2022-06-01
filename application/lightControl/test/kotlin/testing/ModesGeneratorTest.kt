/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.modes.*
import kotlin.math.abs

class ModesGeneratorTest {

  @Test
  fun `test simple mode automaton`() {
    runSimulation(ModeAutomaton("generated")) {
      listOf(-22, 22, -12, 11, -11).forEach {
        input("inputValue").send(Message(it/10))
        tickInputs()
        Assert.assertEquals("[${it}]", abs(it%10), output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }
}