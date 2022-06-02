/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.behavior.statecharts.*
import kotlin.math.abs
import kotlin.math.sign

class StatechartGeneratorTest {

  @Test
  fun `test transition reaction`() {
    runSimulation(TransitionReaction("generated")) {
      (4 .. 7).forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", it * 2, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @org.junit.Test
  fun `test transition guard`() {
    runSimulation(TransitionGuard("generated")) {
      (-2 .. 4).forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        Assert.assertEquals("[$it]", 2 * abs(it), output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test two states`() {
    var factor = 2
    runSimulation(TwoStates("generated")) {
      (-2 .. 4).forEach {
        input("inputValue").send(Message(it))
        tickInputs()
        val expected = factor * it
        factor = 4
        Assert.assertEquals("[$it]", expected, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @org.junit.Test
  fun `test many states first run`() {
    runSimulation(ManyStates("generated")) {
      run1.forEach {
        input("gas").send(Message(it.first))
        tickInputs()
        Assert.assertEquals("[${it.first}]", it.second, output("s").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @org.junit.Test
  fun `test many states other run`() {
    runSimulation(ManyStates("generated")) {
      run2.forEach {
        input("gas").send(Message(it.first))
        tickInputs()
        Assert.assertEquals("[${it.first}]", it.second, output("s").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @org.junit.Test
  fun `test do and exit actions`() {
    runSimulation(DoActions("generated")) {
      listOf(2, 2, -1, 4, 4).forEach {
        input("inputValue").send(Message(sign(it.toDouble()).toInt()))
        tickInputs()
        Assert.assertEquals("[${sign(it.toDouble())}]", it, output("outputValue").receive().payload)
        tickAtOutputs()
      }
    }
  }

  @org.junit.Test
  fun `test order of action execution`() {
    var one = 0
    runSimulation(OrderOfActions("generated")) {
      listOf("took transition", "start-exit-reaction-entry").forEach {
        tickInputs()
        Assert.assertEquals("[$one]", it, output("text").receive().payload)
        tickAtOutputs()
        one = 1
      }
    }
  }

  private val run1: List<Pair<Int, String>> = listOf(
      -125 to "par",
      125 to "acc",
      126 to "bre",
      127 to "loc",
      0 to "par",
      25 to "acc",
      26 to "acc",
      17 to "cru",
      -4 to "bra",
      -5 to "bra",
      24 to "acc",
      -6 to "cru",
      -7 to "bra",
      0 to "par"
  )

  private val run2: List<Pair<Int, String>> = listOf(
      41 to "acc",
      42 to "acc",
      13 to "cru",
      44 to "acc",
      -201 to "cru",
      -202 to "bra",
      -203 to "bre",
      -204 to "loc",
      -205 to "loc",
      0 to "par",
      -206 to "par"
  )

}