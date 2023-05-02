/* (c) https://github.com/MontiCore/monticore */
package bumperbot;

component BumperBot {

  Ultrasonic sensor;
  Motor leftMotor;
  Motor rightMotor;
  BumpControl controller;
  Timer timer (1000);
  Logger logger;

  sensor.distance   -> controller.distance;
  controller.right  -> rightMotor.cmd;
  controller.left   -> leftMotor.cmd;
  controller.timer  -> timer.cmd;
  timer.signal      -> controller.signal;
  controller.speed  -> leftMotor.speed;
  controller.speed  -> rightMotor.speed;
  controller.log    -> logger.message;

}
