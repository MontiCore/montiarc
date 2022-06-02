/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.msg.Message
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test
import testing.modes.reactions.*

class ModeReactionsGeneratorTest {

  @Test
  fun `test connector rerouting`() {
    runSimulation(ChangeConnectors("generated")) {
      connectorRerouting.forEach {
        input("inputValue").send(Message(it.first))
        tickInputs()
        val actual = output("outputValue").receive().payload
        it.second?.let{expected -> Assert.assertEquals("[${it.first}]", expected, actual) }
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test subcomponent addition`() {
    runSimulation(AddSubcomponents("generated")) {
      subCompAddition.forEach {
        input("inputValue").send(Message(it.first))
        tickInputs()
        val actual = output("outputValue").receive().payload
        it.second?.let{expected -> Assert.assertEquals("[${it}]", expected, actual) }
        tickAtOutputs()
      }
    }
  }

  @Test
  fun `test subcomponent deletion`() {
    runSimulation(RemoveSubcomponents("generated", HashMap())) {
      subCompDeletion.forEach {
        input("inputValue").send(Message(it.first))
        tickInputs()
        val actual = output("outputValue").receive().payload
        it.second?.let{expected -> Assert.assertEquals("[${it}]", expected, actual) }
        tickAtOutputs()
      }
    }
  }

  private val connectorRerouting: List<Pair<Int, Int?>> = listOf(
      10 to 30, // +5 then *2
      11 to null,
      12 to 29, // *2 then +5
      13 to 31  // *2 then +5
  )

  private val subCompAddition: List<Pair<Int, Int?>> = listOf(
      0 to 1,
      4 to null,
      0 to null,
      0 to 5,
      -10 to null,
      0 to null,
      0 to -5
  )

  private val subCompDeletion: List<Pair<Int, Int?>> = listOf(
      0 to 1,
      4 to null, // add 4 (+1=5)
      0 to null,
      0 to 5,
      5 to null, // add 5 (+5=10)
      0 to null,
      0 to 10,
      4 to null, // remove 4 (+10=6)
      0 to null,
      0 to 6,
      4 to null, // re-add 4 (+6=10)
      0 to null,
      0 to 10,
  )
}