/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import types.Datatypes.*;
import components.body.subcomponents.Motor;

/*
 * Invalid model. Motor expects a String argument.
 *
 * @implements [Hab16] R10: If a configurable component is instantiated as a subcomponent,
 * all configuration parameters have to be assigned. (p.67, lst. 3.45)
 */
component WrongSubComponentArgument {
    port
        in Integer speed,
        in MotorCommand command;

  component Motor(30);  // Wrong parameter type expected String
  
  connect speed -> motor.speed;
  connect command -> motor.command;   
}