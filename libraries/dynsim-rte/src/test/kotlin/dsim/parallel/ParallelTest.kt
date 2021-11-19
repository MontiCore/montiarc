/* (c) https://github.com/MontiCore/monticore */
package dsim.parallel

import dsim.msg.Message
import dsim.msg.Tick
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Test

class ParallelTest {
  @Test
  fun `test component works as intended`() = runBlocking {
    val dc = SimpleDeterministicTimeSyncComponent("test")
    val spf = launch { dc() }

    val ps1 = dc.getInputPort("string 1")
    val ps2 = dc.getInputPort("string 2")
    val pso = dc.getOutputPort("output string")

    ps1.pushMsg(Message("abcde"))
    ps2.pushMsg(Message("abcfg"))

    ps1.pushMsg(Tick())
    ps2.pushMsg(Tick())

    Assert.assertEquals("abcdefg", pso.pullMsg().payload)

    spf.cancel()
  }

  @Test
  fun `components run deterministically in parallel dispatch`() = runBlocking {
    val dc = SimpleDeterministicTimeSyncComponent("test")
    val spf = launch(Dispatchers.Default) { dc() }

    val ps1 = dc.getInputPort("string 1")
    val ps2 = dc.getInputPort("string 2")
    val pso = dc.getOutputPort("output string")

    repeat(1000) {
      ps1.pushMsg(Message("abcde"))
      ps2.pushMsg(Message("abcfg"))

      ps1.pushMsg(Tick())
      ps2.pushMsg(Tick())
    }

    repeat(1000) {
      Assert.assertEquals("abcdefg", pso.pullMsg().payload)
      Assert.assertTrue(pso.pullMsg() is Tick)
    }

    spf.cancel()
  }

  @Test
  fun `components run faster in parallel dispatch`() = runBlocking<Unit> {
    val dc = SimpleParallelTimeSyncComponent("test")
    //val spf = launch(newSingleThreadContext("stc")) { dc() }
    val spf = launch(Dispatchers.Default) { dc() }

    val pw1 = dc.getInputPort("word1")
    val pw2 = dc.getInputPort("word2")

    val pcc = dc.getOutputPort("sum")

    launch {
      repeat(1000) {
        pw1.pushMsg(Message("abccccdddeff"))
        pw2.pushMsg(Message("yyyyyyyyyyzyyzz"))

        pw1.pushMsg(Tick())
        pw2.pushMsg(Tick())
      }
    }

    val out = launch {
      repeat(1000) {
        Assert.assertEquals(8, pcc.pullMsg().payload)
        Assert.assertTrue(pcc.pullMsg() is Tick)
      }
    }

    out.join()
    spf.cancel()
  }
}
