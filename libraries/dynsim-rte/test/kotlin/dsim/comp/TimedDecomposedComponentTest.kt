/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.msg.Tick
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class TimedDecomposedComponentTest {

  @Test
  fun `timed decomposed schedule forwards ticks`() = runBlocking {
    val dc = TimedDecomposedComponent("dc")
    val spf = launch { dc() }

    val pin1 = dc.getInputPort("input 1")
    val pin2 = dc.getInputPort("input 2")
    val pout = dc.getOutputPort("output 1")

    // Provide input
    pin1.pushMsg(Message("this"))

    pin1.pushMsg(Tick())
    pin2.pushMsg(Tick())

    pin2.pushMsg(Message("should"))
    pin2.pushMsg(Message("definitely"))

    pin1.pushMsg(Tick())
    pin2.pushMsg(Tick())

    pin1.pushMsg(Message("arrive"))

    pin1.pushMsg(Tick())
    pin2.pushMsg(Tick())

    pin2.pushMsg(Message("in"))

    pin1.pushMsg(Tick())
    pin2.pushMsg(Tick())

    pin1.pushMsg(Message("order"))

    // Test output
    Assert.assertEquals("this", pout.pullMsg().payload)
    Assert.assertTrue(pout.pullMsg() is Tick)
    Assert.assertEquals("should", pout.pullMsg().payload)
    Assert.assertEquals("definitely", pout.pullMsg().payload)
    Assert.assertTrue(pout.pullMsg() is Tick)
    Assert.assertEquals("arrive", pout.pullMsg().payload)
    Assert.assertTrue(pout.pullMsg() is Tick)
    Assert.assertEquals("in", pout.pullMsg().payload)
    Assert.assertTrue(pout.pullMsg() is Tick)
    Assert.assertEquals("order", pout.pullMsg().payload)

    spf.cancel()
  }
}