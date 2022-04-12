/* (c) https://github.com/MontiCore/monticore */
package testing

import dsim.comp.NoSuchPortException
import dsim.msg.Message
import dsim.msg.Tick
import dsim.sched.util.CONTROL_PORT
import dsim.sim.ISimulation
import dsim.sim.runSimulation
import lighting.receiveFrom
import org.junit.Assert
import org.junit.Test
import testing.timing.TimedSum
import testing.timing.UntimedSum

class OtherTimingsGeneratorTest {

  @Test
  fun `test timed schedule was generated correctly`() {
    runSimulation(TimedSum("generated")) {
      ignoreControlPort(this)
      // both test-cases have equal input
      input("a").let { it.send(Message(1)); it.send(Tick()) }
      input("a").let { it.send(Message(2)); it.send(Tick()) }
      input("a").let { it.send(Message(3)); it.send(Tick()) }
      input("b").let { it.send(Message(10)); it.send(Tick()) }
      input("b").let { it.send(Message(20)); it.send(Tick()) }
      input("b").let { it.send(Message(30)); it.send(Tick()) }
      Assert.assertEquals("[0]", 1, receiveFrom("c"))
      Assert.assertEquals("[1]", 11, receiveFrom("c"))
      Assert.assertEquals("[2]", 13, receiveFrom("c"))
      Assert.assertEquals("[3]", 33, receiveFrom("c"))
      Assert.assertEquals("[4]", 36, receiveFrom("c"))
      Assert.assertEquals("[5]", 66, receiveFrom("c"))
    }
  }

  @Test
  fun `test untimed schedule was generated correctly`() {
    runSimulation(UntimedSum("generated")) {
      ignoreControlPort(this)
      // ticks have no effect in untimed schedules,
      // but they are here to show, that the inputs are equal
      input("a").let { it.send(Message(1)); it.send(Tick()) }
      input("a").let { it.send(Message(2)); it.send(Tick()) }
      input("a").let { it.send(Message(3)); it.send(Tick()) }
      input("b").let { it.send(Message(10)); it.send(Tick()) }
      input("b").let { it.send(Message(20)); it.send(Tick()) }
      input("b").let { it.send(Message(30)); it.send(Tick()) }
      // output is different from above
      Assert.assertEquals("[0]", 1, receiveFrom("c"))
      Assert.assertEquals("[1]", 3, receiveFrom("c"))
      Assert.assertEquals("[2]", 6, receiveFrom("c"))
      Assert.assertEquals("[3]", 16, receiveFrom("c"))
      Assert.assertEquals("[4]", 36, receiveFrom("c"))
      Assert.assertEquals("[5]", 66, receiveFrom("c"))
    }
  }

  private suspend fun ignoreControlPort(s: ISimulation) {
    val p = try {
      s.input(CONTROL_PORT)
    } catch (e: NoSuchPortException) {
      null
    }
    (0..7).forEach { _ ->
      p?.send(Tick())
    }
  }
}