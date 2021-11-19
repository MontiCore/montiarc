/* (c) https://github.com/MontiCore/monticore */
package example.test

import dsim.msg.Message
import dsim.sim.runSimulation
import example.ilc.LightCtrl
import example.ilc.signals.DoorStatus
import example.ilc.signals.LightCmd
import example.ilc.signals.ModeCmd
import example.ilc.signals.MotorStatus
import org.junit.Assert
import org.junit.Test

class LightCtrlTest {

  @Test
  fun `light control test`() {
    runSimulation(LightCtrl("lightCtrl")) {
      input("doorStatus").send(Message(DoorStatus.OPEN))
      tickInputs()

      Assert.assertEquals(LightCmd.ON, output("cmd").receive().payload)
      Assert.assertTrue(tickAtOutputs())

      input("doorStatus").send(Message(DoorStatus.CLOSED))
      tickInputs()

      Assert.assertEquals(LightCmd.OFF, output("cmd").receive().payload)
      Assert.assertTrue(tickAtOutputs())

      input("modeCmd").send(Message(ModeCmd.COMFORT))
      input("doorStatus").send(Message(DoorStatus.OPEN))
      tickInputs()

      Assert.assertEquals(LightCmd.ON, output("cmd").receive().payload)
      Assert.assertTrue(tickAtOutputs())

      input("motorStatus").send(Message(MotorStatus.IDLE))
      tickInputs()

      Assert.assertEquals(LightCmd.OFF, output("cmd").receive().payload)
      Assert.assertTrue(tickAtOutputs())
    }
  }
}
