package component.body.subcomponents;

import types.Datatypes.*;
import component.body.subcomponents.Motor;

/**
 * Invalid model. Motor expects a String argument.
 */
component WrongSubComponentArgument {
    port
        in Integer speed,
        in MotorCmd command;

  component Motor(30);  // Wrong parameter type expected String
  
  connect speed -> motor.speed;
  connect command -> motor.command;   
}
