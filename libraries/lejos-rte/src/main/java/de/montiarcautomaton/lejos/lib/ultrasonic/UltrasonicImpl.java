package de.montiarcautomaton.lejos.lib.ultrasonic;

import de.montiarcautomaton.lejos.lib.Datatypes.SensorPort;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import lejos.nxt.UltrasonicSensor;

/**
 * Component behavior implementation of the bumperbot ultrasonic sensor.
 * 
 * @author Gerrit Leonhardt
 */
public class UltrasonicImpl implements IComputable<UltrasonicInput, UltrasonicResult> {
  private UltrasonicSensor nxtUltrasonicSensor;
  private SensorPort sensorPort;
  
  public UltrasonicImpl(SensorPort sensorPort) {
    this.sensorPort = sensorPort;
    this.nxtUltrasonicSensor = new UltrasonicSensor(toNxtPort(this.sensorPort));
    this.nxtUltrasonicSensor.continuous();
  }
  
  private static lejos.nxt.SensorPort toNxtPort(SensorPort port) {
    switch (port) {
      case S1:
        return lejos.nxt.SensorPort.S1;
      case S2:
        return lejos.nxt.SensorPort.S2;
      case S3:
        return lejos.nxt.SensorPort.S3;
      case S4:
        return lejos.nxt.SensorPort.S4;
      default:
        throw new RuntimeException("Invalid sensor port.");
    }
  }
  
  @Override
  public UltrasonicResult getInitialValues() {
    return new UltrasonicResult();
  }
  
  @Override
  public UltrasonicResult compute(UltrasonicInput input) {
    return new UltrasonicResult(nxtUltrasonicSensor.getDistance());
  }
  
}
