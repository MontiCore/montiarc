/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.motor;

import montiarc.lejos.lib.Datatypes.MotorPort;
import montiarc.lejos.lib.motor.Datatypes.MotorCmd;

component Motor(MotorPort motorPort) {
  port
    in MotorCmd cmd,
    in Integer speed;

}
