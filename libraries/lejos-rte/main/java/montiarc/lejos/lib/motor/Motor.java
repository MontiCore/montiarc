/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.motor;

import montiarc.lejos.lib.MotorPort;
import lejos.nxt.NXTMotor;

/**
 * Component behavior implementation of a bumperbot motor.
 * 
 */
public class Motor extends MotorTOP {
  private final int power;
  private final NXTMotor nxtMotor;
  
  public Motor(MotorPort motorPort) {
    this(motorPort, 50);
  }
  
  public Motor(MotorPort motorPort, int power) {
    super(motorPort);
    this.power = power;
    
    this.nxtMotor = new NXTMotor(toNxtPort(this.motorPort));
    this.nxtMotor.setPower(this.power);
    this.nxtMotor.stop();
  }
  
  private static lejos.nxt.MotorPort toNxtPort(MotorPort port) {
    switch (port) {
      case A:
        return lejos.nxt.MotorPort.A;
      case B:
        return lejos.nxt.MotorPort.B;
      case C:
        return lejos.nxt.MotorPort.C;
      default:
        throw new RuntimeException("Innvalid motor port.");
    }
  }

  @Override
  public void compute() {
    if (this.getSpeed() != null) {
      nxtMotor.setPower(this.getSpeed().getValue());
    }
    
    if (this.getCmd().getValue() == MotorCmd.FORWARD) {
      nxtMotor.forward();
    }
    else if (this.getCmd().getValue() == MotorCmd.BACKWARD) {
      nxtMotor.backward();
    }
    else if (this.getCmd().getValue() == MotorCmd.STOP) {
      nxtMotor.stop();
    }
  }
  
}
