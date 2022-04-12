/* (c) https://github.com/MontiCore/monticore */
package lighting

import dsim.msg.Message
import dsim.msg.Tick
import dsim.sim.ISimulation
import dsim.sim.runSimulation
import org.junit.Assert
import org.junit.Test

class EnlightenedCarTest {

  @Test
  fun `full light test`() {
    runSimulation(EnlightenedCar("lightCar")) {
      (1..10).forEach {
        println("Tick $it")
        tickInputs()
        receiveFrom("mirrorLight")
            .let { t -> t as String }
            .also { t -> Assert.assertTrue("[$it], $t", t.startsWith("mirror")) }
        receiveFrom("doorLight")
            .let { t -> t as String }
            .also { t -> Assert.assertTrue("[$it], $t", t.startsWith("door")) }
      }
    }
  }

  @Test
  fun `control test`() {
    runSimulation(LightCtrl("lightCtrl")) {
      (1..6).forEach {
        Assert.assertEquals("[${it}]", texts[it * 2 - 2], receiveFrom("mirror"))
        Assert.assertEquals("[${it}]", texts[it * 2 - 1], receiveFrom("door"))
        println("---- send m ___ ${it % 3 == 0}")
        println("---- send d ___ ${it % 2 == 0}")
        input("changeMode").send(Message(it % 3 == 0))
        input("doorOpen").send(Message(it % 2 == 0))
        tickInputs()
      }
    }
  }

  private val texts = listOf(
      "mirror-light idle",
      "door-light idle",
      "mirror-light stays off",
      "door-light stays off",
      "mirror-light turned on",
      "door-light turned on",
      "mirror-light turned off",
      "door-light turned off",
      "mirror-light turned on",
      "door-light stays off",
      "mirror-light stays on",
      "door-light stays off"
  )
}

suspend fun ISimulation.receiveFrom(port: String): Any {
  var number = 0
  do {
    val msg = output(port).receive()
    if (msg is Tick) {
      continue
    }
    if (msg is Message) {
      return msg.payload
    }

  } while (++number < 10)
  throw RuntimeException("only ticks received")
}