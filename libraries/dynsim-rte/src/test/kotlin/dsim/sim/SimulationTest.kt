/* (c) https://github.com/MontiCore/monticore */
package dsim.sim

import dsim.comp.ExampleAtomicComponent
import dsim.msg.Message
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class SimulationTest {
  @Test
  fun `simulation objects can stop simulation and terminate`() = runBlocking {
    val sim = backgroundSimulation(ExampleAtomicComponent("test"))

    sim.stop()
  }

  @Test
  fun `inlined simulation test code removes need for coroutines specific code`() {
    runSimulation(ExampleAtomicComponent("test")) {
      inputs["oof"]?.send(Message("abc"))

      Assert.assertNotNull(outputs["foo"])
      Assert.assertEquals("cba", outputs["foo"]?.receive()?.payload)
    }
  }
}
