package de.montiarcautomaton.lejos.lib.motor;

import de.montiarcautomaton.lejos.lib.Datatypes.MotorPort;
import de.montiarcautomaton.lejos.lib.motor.Datatypes.MotorCmd;

component Motor(MotorPort motorPort) {
  port
    in MotorCmd cmd,
    in Integer speed;

}