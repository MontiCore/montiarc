package bumperbot;

import bumperbot.library.Motor;
import bumperbot.library.DistanceSensor;
import bumperbot.library.Timer;

<<deploy>> component BumperBot {

  // BumperBot movement components

  component DistanceSensor sensor;
  component Motor leftMotor;
  component Motor rightMotor;    
  component BumpControl controller;  
  //component Timer(500) timer;
  
  connect sensor.distance   -> controller.distance;
  connect controller.right  -> leftMotor.speed;
  connect controller.left   -> rightMotor.speed;
  //connect controller.timer  -> timer.timerCmd;  
  //connect timer.timerSignal -> controller.signal;
  
  // BumperBot demonstration components with generics and parameters


  //connect controller.left  -> logging.leftMotorInput;
  //connect controller.right -> logging.rightMotorInput;
}
