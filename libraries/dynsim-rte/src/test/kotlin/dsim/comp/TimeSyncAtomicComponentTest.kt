/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.msg.Tick
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class TimeSyncAtomicComponentTest {
  @Test
  fun `time-sync atomic components process messages batch-wise`() = runBlocking {
    val ac = ExampleTimeSyncAtomicComponent("test")

    val spf = launch { ac() }

    val a = ac.getInputPort("a")
    Assert.assertNotNull(a)

    val b = ac.getInputPort("b")
    Assert.assertNotNull(b)

    val x = ac.getOutputPort("x")
    Assert.assertNotNull(x)

    launch {
      a.pushMsg(Message("a"))
      a.pushMsg(Tick())
      a.pushMsg(Message("b"))
      a.pushMsg(Tick())
      a.pushMsg(Message("c"))
      a.pushMsg(Tick())
      a.pushMsg(Message("d"))
      a.pushMsg(Tick())
    }

    launch {
      b.pushMsg(Message(1))
      b.pushMsg(Tick())
      b.pushMsg(Message(2))
      b.pushMsg(Tick())
      b.pushMsg(Message(3))
      b.pushMsg(Tick())
      b.pushMsg(Message(4))
      b.pushMsg(Tick())
    }

    val out = launch {
      Assert.assertEquals("a1", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)
      Assert.assertEquals("b2", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)
      Assert.assertEquals("c3", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)
      Assert.assertEquals("d4", x.pullMsg().payload)
      Assert.assertTrue(x.pullMsg() is Tick)
    }

    out.join()
    spf.cancel()
  }

  @Test
  fun `time-sync atomic components only accept one message per time frame`() = runBlocking {
    val ac = ExampleTimeSyncAtomicComponent("test")

    val spf = launch { ac() }

    val a = ac.getInputPort("a")
    Assert.assertNotNull(a)

    val b = ac.getInputPort("b")
    Assert.assertNotNull(b)

    val x = ac.getOutputPort("x")
    Assert.assertNotNull(x)

    launch {
      a.pushMsg(Message("a"))
      a.pushMsg(Message("b"))
      a.pushMsg(Message("c"))
      a.pushMsg(Tick())
    }

    launch {
      b.pushMsg(Message(1))
      b.pushMsg(Tick())
    }
    Assert.assertEquals("c1", x.pullMsg().payload)

    spf.cancel()
  }
}
