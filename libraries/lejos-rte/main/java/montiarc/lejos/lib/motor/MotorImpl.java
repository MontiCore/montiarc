/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.motor;

import montiarc.lejos.lib.motor.MotorCmd;
import montiarc.lejos.lib.motor.MotorInput;
import montiarc.lejos.lib.motor.MotorResult;
import de.montiarc.runtimes.timesync.implementation.IComputable;
import montiarc.lejos.lib.MotorPort;
import lejos.nxt.NXTMotor;

/**
 * Component behavior implementation of a bumperbot motor.
 * 
 */
public class MotorImpl implements IComputable<MotorInput, MotorResult> {
  private final int power;
  private final MotorPort motorPort;
  private final NXTMotor nxtMotor;
  
  public MotorImpl(MotorPort motorPort) {
    this(motorPort, 50);
  }
  
  public MotorImpl(MotorPort motorPort, int power) {
    this.motorPort = motorPort;
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
  public MotorResult getInitialValues() {
    return new MotorResult();
  }
  
  @Override
  public MotorResult compute(MotorInput input) {
    if (input.getSpeed() != null) {
      nxtMotor.setPower(input.getSpeed());
    }
    
    if (input.getCmd() == MotorCmd.FORWARD) {
      nxtMotor.forward();
    }
    else if (input.getCmd() == MotorCmd.BACKWARD) {
      nxtMotor.backward();
    }
    else if (input.getCmd() == MotorCmd.STOP) {
      nxtMotor.stop();
    }
    
    return new MotorResult();
  }
  
}
