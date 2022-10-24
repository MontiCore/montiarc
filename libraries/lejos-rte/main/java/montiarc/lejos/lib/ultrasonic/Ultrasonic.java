/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.ultrasonic;

import montiarc.lejos.lib.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * Component behavior implementation of the bumperbot ultrasonic sensor.
 * 
 */
public class Ultrasonic extends UltrasonicTOP {
  private final UltrasonicSensor nxtUltrasonicSensor;
  
  public Ultrasonic(SensorPort sensorPort) {
    super(sensorPort);
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
  public void compute() {
    this.getDistance().setValue(nxtUltrasonicSensor.getDistance());
  }
  
}
