/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.msg.Message
import dsim.port.util.port
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class PortTest {

  @Test
  fun `sending message to incompatible port fails`() = runBlocking {
    val p = port<String>("test port")

    try {
      p.pushMsg(Message(1))
      Assert.fail()
    } catch (e: IncompatibleMessageTypeException) {
    }
  }

  @Test
  fun `sending message subtype to port type succeeds`() = runBlocking<Unit> {
    open class A
    class B : A()

    val p = port<A>("A-port")

    try {
      launch { p.pushMsg(Message(B())) }
      launch { p.pullMsg() }
    } catch (e: IncompatibleMessageTypeException) {
      Assert.fail()
    }
  }
}
