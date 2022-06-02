/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.structure.composed.*

class CompositionGeneratorTest {

  private val list = listOf(2, 4, -1, 1)

  @Test
  fun `test forwarding to output port`() {
    runSimulation(OutputPortForward("generated")) {
      list.forEach {
        tickInputs()
        Assert.assertEquals("[${it}]", 1, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test forwarding from input port`() {
    runSimulation(InputPortForward("generated")) {
      list.forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[${it}]", it * 2, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test parameter specification`() {
    runSimulation(ParameterStatement("generated")) {
      list.forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[${it}]", it + 4, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test parameter delegation`() {
    runSimulation(ParameterDelegation("generated", 10)) {
      list.forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[${it}]", it + 10, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test components with two subcomponents`() {
    runSimulation(TwoSubComponents("generated")) {
      list.forEach {
        tickInputs()
        Assert.assertEquals("[${it}]", 2, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }
}