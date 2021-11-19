/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.msg.Tick
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class TimedAtomicComponentTest {
  @Test
  fun `timed components process messages according to time frames`() = runBlocking {
    val ac = ExampleTimedAtomicComponent("test")

    val sim = launch { ac() }

    val a = ac.getInputPort("a")
    val b = ac.getInputPort("b")

    val x = ac.getOutputPort("x")

    launch {
      a.pushMsg(Tick())

      a.pushMsg(Message("test message"))
      a.pushMsg(Tick())


      a.pushMsg(Tick())
    }

    launch {
      b.pushMsg(Message("1"))
      b.pushMsg(Tick())

      b.pushMsg(Tick())

      b.pushMsg(Message("2"))
      b.pushMsg(Tick())
    }

    val out = launch {
      Assert.assertEquals("1", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)

      Assert.assertEquals("test message", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)

      Assert.assertEquals("2", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)
    }

    out.join()

    sim.cancel()
  }
}