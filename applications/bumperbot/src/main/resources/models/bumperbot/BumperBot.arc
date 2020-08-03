/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

import de.montiarcautomaton.lejos.lib.Datatypes.MotorPort;
import de.montiarcautomaton.lejos.lib.Datatypes.SensorPort;

import de.montiarcautomaton.lejos.lib.timer.Timer;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerCmd;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerSignal;

import de.montiarcautomaton.lejos.lib.motor.Motor;
import de.montiarcautomaton.lejos.lib.motor.Datatypes.MotorCmd;

import de.montiarcautomaton.lejos.lib.ultrasonic.Ultrasonic;

import de.montiarcautomaton.lejos.lib.logger.Logger;

  component BumperBot {
  Ultrasonic sensor (SensorPort.S1); // use port S1
  Motor leftMotor (MotorPort.A); // use port A
  Motor rightMotor (MotorPort.B); // use port B
  BumpControl controller;
  Timer timer (1000); // 1 sec delay
  Logger logger;

  sensor.distance   -> controller.distance;
  controller.right  -> leftMotor.cmd;
  controller.left   -> rightMotor.cmd;
  controller.timer  -> timer.cmd;
  timer.signal      -> controller.signal;
  controller.speed  -> leftMotor.speed;
  controller.speed  -> rightMotor.speed;
  controller.log    -> logger.message;
}