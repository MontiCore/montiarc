/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.msg.Tick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FunctionalSchedulerTest {

  @Test
  fun `untimed functional scheduler works statically`() = runBlocking {
    val ac = FunctionalScheduledAtomicComponent("test")

    val pin = ac.getInputPort("input")
    val pout = ac.getOutputPort("output")

    val spf = launch { ac() }

    launch {
      pin.pushMsg(Message("a"))
      pin.pushMsg(Message("b"))
      pin.pushMsg(Message("c"))
    }

    val out = launch {
      Assert.assertEquals("a", pout.pullMsg().payload)
      Assert.assertEquals("b", pout.pullMsg().payload)
      Assert.assertEquals("c", pout.pullMsg().payload)
    }

    out.join()
    spf.cancel()
  }

  @Test
  fun `timed functional scheduler works statically`() = runBlocking {
    val ac = FunctionalScheduledTimedAtomicComponent("test")

    val pin1 = ac.getInputPort("1")
    val pin2 = ac.getInputPort("2")
    val pout = ac.getOutputPort("out")

    val spf = launch { ac() }

    launch {

      pin1.pushMsg(Tick())

      pin1.pushMsg(Message("a"))
      pin1.pushMsg(Message("b"))

      pin1.pushMsg(Tick())
    }

    launch {
      pin2.pushMsg(Message("1"))

      pin2.pushMsg(Tick())

      pin2.pushMsg(Message("2"))

      pin2.pushMsg(Tick())
    }

    val out = launch {
      Assert.assertEquals("1", pout.pullMsg().payload)

      Assert.assertTrue(pout.pullMsg() is Tick)

      val frame2 = setOf(
          pout.pullMsg().payload,
          pout.pullMsg().payload,
          pout.pullMsg().payload
      )

      // No tick message in frame 2
      Assert.assertTrue(null !in frame2)

      Assert.assertTrue(frame2.contains("2"))
      Assert.assertTrue(frame2.contains("a"))
      Assert.assertTrue(frame2.contains("b"))

      Assert.assertTrue(pout.pullMsg() is Tick)
    }

    out.join()
    spf.cancel()
  }

  @Test
  fun `new events are only scheduled on request`() = runBlocking {
    val ac = FunctionalScheduledDynamicAtomicComponent("test")
    val spf = launch { ac() }

    val pin = ac.getInputPort("input")
    val pout = ac.getOutputPort("output")

    launch {
      pin.pushMsg(Message("before reconfiguration"))
      pin.pushMsg(Message("new input"))

      delay(100)
      val pinNew = ac.getInputPort("new input")

      pinNew.pushMsg(Message("after reconfiguration"))
    }

    val out = launch {
      Assert.assertEquals("before reconfiguration", pout.pullMsg().payload)
      Assert.assertEquals("after reconfiguration", pout.pullMsg().payload)
    }

    out.join()
    spf.cancel()
  }
}
