/* (c) https://github.com/MontiCore/monticore */
package speedbumper;

import montiarc.lejos.lib.MotorPort;
import montiarc.lejos.lib.SensorPort;

import montiarc.lejos.lib.timer.Timer;
import montiarc.lejos.lib.timer.TimerCmd;
import montiarc.lejos.lib.timer.TimerSignal;

import montiarc.lejos.lib.motor.Motor;
import montiarc.lejos.lib.motor.MotorCmd;

import montiarc.lejos.lib.ultrasonic.Ultrasonic;
import montiarc.lejos.lib.logger.Logger;

import java.lang.Integer;

component BumperBot {
  Ultrasonic sensor (SensorPort.S1); // use port S1
  Motor leftMotor (MotorPort.A); // use port A
  Motor rightMotor (MotorPort.B); // use port B
  BumpControl controller;
  BumpSpeed speedCtrl (30);
  Timer timer (1000); // 1 sec delay
  Logger logger;

  sensor.distance   -> controller.distance;
  sensor.distance   -> speedCtrl.distance;
  controller.right  -> leftMotor.cmd;
  controller.left   -> rightMotor.cmd;
  controller.timer  -> timer.cmd;
  controller.speedCmd  -> speedCtrl.cmd;
  timer.signal      -> controller.signal;
  speedCtrl.speed  -> leftMotor.speed;
  speedCtrl.speed  -> rightMotor.speed;
  controller.log    -> logger.message;
}