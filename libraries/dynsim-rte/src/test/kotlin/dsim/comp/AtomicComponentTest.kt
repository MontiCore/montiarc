/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import dsim.msg.Message
import dsim.port.IDataSink
import dsim.port.IDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert
import org.junit.Test

class AtomicComponentTest {

  @Test
  fun `the general idea of spf coroutines makes sense and works the way I intended it to`() = runBlocking {
    val i = Channel<String>()
    val o = Channel<String>()

    // This be the spf
    val spf = launch {
      while (isActive) {
        val msg = i.receive()
        o.send(msg.reversed())
      }
    }

    launch {
      i.send("abcde")
      i.send("x")
    }

    val outcr = launch {
      val m = o.receive()
      Assert.assertEquals("edcba", m)
      val n = o.receive()
      Assert.assertNotEquals("y", n)
    }

    outcr.join() // Wait for out to finish
    spf.cancel() // Then cancel spf so test terminates
  }


  @Test
  fun `coroutines based spfs can be fully cancelled`() = runBlocking {
    val ac = ExampleAtomicComponent("test")

    val spf = launch { ac() }

    spf.cancel()
  }

  @Test
  fun `coroutines based atomic components process messages automatically`() = runBlocking {
    // Constructing component
    val ac = ExampleAtomicComponent("test")

    // Launch component's spf
    val spf = launch { ac() }

    val pin: IDataSink? = ac.getInputPort("oof")
    Assert.assertNotNull(pin)

    val pout: IDataSource? = ac.getOutputPort("foo")
    Assert.assertNotNull(pout)

    // Input Coroutine
    launch {
      pin?.pushMsg(Message("abcde"))
    }

    // Output coroutine
    val out = launch {
      val m = pout?.pullMsg()

      Assert.assertNotNull(m)
      Assert.assertTrue(m is Message)
      Assert.assertEquals("edcba", m?.payload)
    }

    // wait for out to finish
    out.join()

    // Then cancel simulated spf
    spf.cancel()
  }

  @Test
  fun `bundle processing spfs can be modeled`() = runBlocking {
    val ac = StreamBundleAtomicComponent("test")

    val a = ac.getInputPort("a")
    val b = ac.getInputPort("b")

    val x = ac.getOutputPort("x")
    val y = ac.getOutputPort("y")

    // Activate component spf as coroutine
    val spf = launch { ac() }

    // Provide input by coroutine
    launch {
      a.pushMsg(Message("something"))
      b.pushMsg(Message("something else"))
      a.pushMsg(Message("something entirely different"))
    }

    // Check output by coroutine
    val out = launch {

      // Order of processing between several ports may not be equal to order of arrival

      val xs = setOf(
          x.pullMsg().payload,
          x.pullMsg().payload,
          x.pullMsg().payload
      )

      val ys = setOf(
          y.pullMsg().payload,
          y.pullMsg().payload,
          y.pullMsg().payload
      )

      Assert.assertTrue(null !in xs)
      Assert.assertTrue(null !in ys)

      Assert.assertTrue("something" in xs)
      Assert.assertTrue("something else" in xs)
      Assert.assertTrue("something entirely different" in xs)

      Assert.assertTrue(1 in ys)
      Assert.assertTrue(2 in ys)
      Assert.assertTrue(3 in ys)
    }

    // wait for output coroutine to finish
    out.join()

    // Then cancel spf
    spf.cancel()
  }

  @Test
  fun `untimed component maintains order`() = runBlocking {
    val ac = ExampleAtomicComponent("test")

    val spf = launch { ac() }

    val pin: IDataSink = ac.getInputPort("oof")
    Assert.assertNotNull(pin)

    val pout: IDataSource = ac.getOutputPort("foo")
    Assert.assertNotNull(pout)

    for (char in 'a'..'z') {
      pin.pushMsg(Message(char.toString()))
    }

    for (res in 'a'..'z') {
      Assert.assertEquals(res.toString(), pout.pullMsg().payload)
    }

    spf.cancel()
  }

  @Test
  fun `atomic component can reconfigure ports`() = runBlocking {
    val ac = PortReconfiguringAtomicComponent("test")

    val mainIn = ac.getInputPort("input main")

    val spf = launch { ac() }

    try {
      ac.getInputPort("input 2")
      Assert.fail()
    } catch (e: NoSuchPortException) {
      // good
    }

    mainIn.pushMsg(Message("add input"))

    delay(50)

    Assert.assertNotNull(ac.getInputPort("input 2"))

    spf.cancel()
  }

  @Test
  fun `ports added at runtime are scheduled`() = runBlocking {
    val ac = PortReconfiguringAtomicComponent("test")

    val mainIn = ac.getInputPort("input main")
    Assert.assertNotNull(mainIn)

    val counterOut = ac.getOutputPort("counter")
    Assert.assertNotNull(counterOut)

    val spf = launch { ac() }

    mainIn.pushMsg(Message("add input"))

    delay(50)

    val secondIn = ac.getInputPort("input 2")
    Assert.assertNotNull(secondIn)

    launch { secondIn.pushMsg(Message("get count")) }

    val m = counterOut.pullMsg()
    Assert.assertEquals(2, m.payload)

    spf.cancel()
  }
}
