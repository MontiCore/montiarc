package architecture;

import robotmodels.actuators.Motor;
import robotmodels.sensors.DistanceSensor;
import robotmodels.time.Timer;

deploy component BumperBot {

  // BumperBot movement components

  component DistanceSensor sensor;
  component Motor leftMotor;
  component Motor rightMotor;    
  component BumpControl controller;
  component Timer(500) timer;
  
  connect sensor.distance   -> controller.distance;
  connect controller.right  -> leftMotor.cmd;
  connect controller.left   -> rightMotor.cmd;
  connect controller.timer  -> timer.timerCmd;  
  connect timer.timerSignal -> controller.signal;
  
  // BumperBot demonstration components with generics and parameters

  component Logging logging;

  connect controller.left  -> logging.leftMotorInput;
  connect controller.right -> logging.rightMotorInput;
}
