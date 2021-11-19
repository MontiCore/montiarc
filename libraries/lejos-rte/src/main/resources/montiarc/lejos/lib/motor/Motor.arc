/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.motor;

import montiarc.lejos.lib.MotorPort;
import montiarc.lejos.lib.motor.MotorCmd;

import java.lang.Integer;

component Motor(MotorPort motorPort) {
  port
    in MotorCmd cmd,
    in Integer speed;

}
