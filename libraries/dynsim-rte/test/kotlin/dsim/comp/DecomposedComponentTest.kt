/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DecomposedComponentTest {
  @Test
  fun `static untimed decomposed components are initialized and started without error`() = runBlocking {
    val dc = ExampleStaticDecomposedComponent("test")

    val spf = launch { dc() }

    spf.cancel()
  }

  @Test
  fun `static untimed decomposed components forward messages along connectors`() = runBlocking {
    val dc = ExampleStaticDecomposedComponent("test")

    val spf = launch { dc() }

    val pIn = dc.getInputPort("in")
    Assert.assertNotNull(pIn)
    val pOut = dc.getOutputPort("out")
    Assert.assertNotNull(pOut)

    launch { pIn.pushMsg(Message("abc")) }

    val out = launch {
      Assert.assertEquals("cba", pOut.pullMsg().payload)
    }

    out.join()
    spf.cancel()
  }

  @Test
  fun `fan-in connectors do not obstruct scheduling`() = runBlocking {
    val dc = ExampleStaticDecomposedComponent("test")

    val spf = launch { dc() }

    val pIn = dc.getInputPort("in")
    val pInAlt = dc.getInputPort("alt")
    val pOut = dc.getOutputPort("out")

    launch {
      pIn.pushMsg(Message("a"))
      pInAlt.pushMsg(Message("b"))
    }

    val out = launch {
      Assert.assertEquals("a", pOut.pullMsg().payload)
      Assert.assertEquals("b", pOut.pullMsg().payload)
    }

    out.join()
    spf.cancel()
  }

  @Test
  fun `mode automaton components reconfigure as specified`() = runBlocking {
    val mac = ExampleModeAutomatonComponent("test")

    val spf = launch { mac() }

    val pIn = mac.getInputPort("input")
    val pSw = mac.getInputPort("switch")
    val pOut1 = mac.getOutputPort("output1")
    val pOut2 = mac.getOutputPort("output2")

    // Should end up at output 1
    pIn.pushMsg(Message("m1"))
    Assert.assertEquals("m1", pOut1.pullMsg().payload)

    // Should change mode
    pSw.pushMsg(Message("switch to B"))

    pIn.pushMsg(Message("m2"))
    Assert.assertEquals("m2", pOut2.pullMsg().payload)

    pSw.pushMsg(Message("switch to A"))

    pIn.pushMsg(Message("m3"))
    Assert.assertEquals("m3", pOut1.pullMsg().payload)

    spf.cancel()
  }

  @Test
  fun `mode automaton components reconfigure with subcomponents as specified`() = runBlocking {
    val mac = ExampleModeAutomatonComponentWithSubcomponent("test")

    val spf = launch { mac() }

    val pIn = mac.getInputPort("input")
    val pCom = mac.getInputPort("com")
    val pOut = mac.getOutputPort("output")

    // Should end up at output 1
    pIn.pushMsg(Message("abc"))
    Assert.assertEquals("cba", pOut.pullMsg().payload)

    // Should change mode
    pCom.pushMsg(Message("pass"))

    pIn.pushMsg(Message("abc"))
    Assert.assertEquals("abc", pOut.pullMsg().payload)

    pCom.pushMsg(Message("invert"))

    pIn.pushMsg(Message("abc"))
    Assert.assertEquals("cba", pOut.pullMsg().payload)

    spf.cancel()
  }

  @Test
  fun `added ports of subcomponents get scheduled`() = runBlocking {

    val dc = DecomposedComponentWithPortReconfiguringSubcomponent("test")
    val spf = launch { dc() }

    val pin = dc.getInputPort("input")
    val pout = dc.getOutputPort("output")

    pin.pushMsg(Message("before adding id port"))
    pin.pushMsg(Message("add id port"))
    pin.pushMsg(Message("after adding id port"))

    Assert.assertEquals("after adding id port", pout.pullMsg().payload)

    spf.cancel()
  }
}
