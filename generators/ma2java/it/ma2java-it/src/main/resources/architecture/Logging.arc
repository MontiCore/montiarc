package architecture;

import robotmodels.actuators.types.MotorCommand;
import robotmodels.io.Logger;

component Logging {

  port
    in MotorCommand leftMotorInput,
    in MotorCommand rightMotorInput;

  component Logger<MotorCommand>("Left Motor") leftMotorLogger;
  component Logger<MotorCommand>("Right Motor") rightMotorLogger;
  
  connect leftMotorInput -> leftMotorLogger.message;
  connect rightMotorInput -> rightMotorLogger.message;
}
