package bumperbot;

//import models.MotorCommand;
import bumperbot.library.Logger;

component Logging {

  port
    in String leftMotorInput,
    in String rightMotorInput;

  component Logger<String>("Left Motor") leftMotorLogger;
  component Logger<String>("Right Motor") rightMotorLogger;
  
  connect leftMotorInput -> leftMotorLogger.message;
  connect rightMotorInput -> rightMotorLogger.message;
}
