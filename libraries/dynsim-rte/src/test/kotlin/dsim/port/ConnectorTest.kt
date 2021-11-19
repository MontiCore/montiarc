/* (c) https://github.com/MontiCore/monticore */
package dsim.port

import dsim.port.util.IncompatiblePortsException
import dsim.port.util.connector
import dsim.port.util.port
import org.junit.Assert
import org.junit.Test

class ConnectorTest {
  @Test
  fun `creating a connector between incompatible ports throws exception`() {
    val p1 = port<String>("port one")
    val p2 = port<Int>("port two")

    try {
      connector(p1, p2)
      Assert.fail()
    } catch (e: IncompatiblePortsException) {
    }
  }

  @Test
  fun `creating a connector from a subtype to a supertype is legal`() {
    open class Super
    class Sub : Super()

    val p1 = port<Sub>("port one")
    val p2 = port<Super>("port two")

    try {
      connector(p1, p2)
    } catch (e: IncompatiblePortsException) {
      Assert.fail()
    }
  }
}
