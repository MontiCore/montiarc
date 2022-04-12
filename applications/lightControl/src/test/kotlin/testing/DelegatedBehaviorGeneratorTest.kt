/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.behavior.delegated.*

class DelegatedBehaviorGeneratorTest {

  @Test
  fun `test behavior delegation to kotlin class`() {
    runSimulation(KotlinBehaviorDelegate("generated")) {
      kotlinValues.forEach { (i, o) ->
        input("inputValue").send(Message(i))
        tickInputs()
        Assert.assertEquals("[$i]", o, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test behavior delegation to java class`() {
    runSimulation(JavaBehaviorDelegate("generated")) {
      javaValues.forEach { (i, o) ->
        input("inputValue").send(Message(i))
        tickInputs()
        Assert.assertEquals("[$i]", o, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test behavior delegation to parameterized class`() {
    runSimulation(ParameterizedBehaviorDelegate("generated", 200)) {
      parameterizedValues.forEach { (i, o) ->
        input("inputValue").send(Message(i))
        tickInputs()
        Assert.assertEquals("[$i]", o, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test behavior delegation to non-stateless class`() {
    runSimulation(StateHavingBehaviorDelegate("generated")) {
      memoryValues.forEach { (i, o) ->
        input("inputValue").send(Message(i))
        tickInputs()
        Assert.assertEquals("[$i]", o, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  private val kotlinValues: List<Pair<Int, Int?>> = listOf(
      0 to 50,
      2 to 52,
      4 to 54
  )

  private val javaValues: List<Pair<Int, Int?>> = listOf(
      2 to 8,
      5 to 20,
      -1 to -4
  )

  private val memoryValues: List<Pair<Int, Int?>> = listOf(
      2 to 1,
      5 to 2,
      -1 to 10,
      6 to -10,
      -1 to -60,
      -1 to 60
  )

  private val parameterizedValues: List<Pair<Int, Int?>> = listOf(
      8 to 408,
      20 to 420,
      -1 to 399
  )
}