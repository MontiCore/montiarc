/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.ultrasonic;

import montiarc.lejos.lib.SensorPort;

import java.lang.Integer;

component Ultrasonic(SensorPort sensorPort) {
  port
    out Integer distance;

}
