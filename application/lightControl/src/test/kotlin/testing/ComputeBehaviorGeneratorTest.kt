/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.behavior.simple.ComputeBehavior
import testing.behavior.simple.ParameterizedBehavior
import testing.behavior.simple.VariableSupportedBehavior

class ComputeBehaviorGeneratorTest {

  @Test
  fun `test simple compute behavior generation`() {
    runSimulation(ComputeBehavior("generated")) {
      (1..4).forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", it, output("outputValue").receive().payload)
        Assert.assertTrue(tickAtOutputs())
      }
    }
  }

  @Test
  fun `test parameter and compute behavior generation`() {
    val parameter = 30
    runSimulation(ParameterizedBehavior("generated", parameter)) {
      (1..4).forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", it + parameter, output("outputValue").receive().payload)
        Assert.assertTrue(tickAtOutputs())
      }
    }
  }

  @Test
  fun `test variable and compute behavior generation`() {
    runSimulation(VariableSupportedBehavior("generated")) {
      expectedValues.forEach {
        input("inputValue").send(Message(it.first))
        tickInputs()
        Assert.assertEquals("[${it.first}]", it.second, output("outputValue").receive().payload)
        Assert.assertTrue(tickAtOutputs())
      }
    }
  }

  private val expectedValues: List<Pair<Int, Int>> = listOf(
      3 to 6,
      5 to 13,
      -1 to 6,
      100 to 207
  )
}