/* (c) https://github.com/MontiCore/monticore */
package speedbumper;

import de.montiarcautomaton.lejos.lib.Datatypes.MotorPort;
import de.montiarcautomaton.lejos.lib.Datatypes.SensorPort;

import de.montiarcautomaton.lejos.lib.timer.Timer;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerCmd;
import de.montiarcautomaton.lejos.lib.timer.Datatypes.TimerSignal;

import de.montiarcautomaton.lejos.lib.motor.Motor;
import de.montiarcautomaton.lejos.lib.motor.Datatypes.MotorCmd;

import de.montiarcautomaton.lejos.lib.ultrasonic.Ultrasonic;

import de.montiarcautomaton.lejos.lib.logger.Logger;

<<deploy>> component BumperBot {
  component Ultrasonic(SensorPort.S1) sensor; // use port S1
  component Motor(MotorPort.A) leftMotor; // use port A
  component Motor(MotorPort.B) rightMotor; // use port B
  component BumpControl controller;  
  component BumpSpeed(30) speedCtrl;  
  component Timer(1000) timer; // 1 sec delay
  component Logger logger;
  
  connect sensor.distance   -> controller.distance;
  connect sensor.distance   -> speedCtrl.distance;
  connect controller.right  -> leftMotor.cmd;
  connect controller.left   -> rightMotor.cmd;
  connect controller.timer  -> timer.cmd;  
  connect controller.speedCmd  -> speedCtrl.cmd;  
  connect timer.signal      -> controller.signal;
  connect speedCtrl.speed  -> leftMotor.speed;
  connect speedCtrl.speed  -> rightMotor.speed;
  connect controller.log    -> logger.message;
}
