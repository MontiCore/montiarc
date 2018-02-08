package components.body.subcomponents;

import types.Datatypes.*;
import components.body.subcomponents.Motor;

/**
 * Invalid model. Motor expects a String argument.
 *
 * TODO: Pruefen unter welche CoCo dieser Test genau faellt. Moeglich:
 * @implements [Wor16] MT7: Default values of parameters conform to their type. (p. 64 Lst. 4.22)
 */
component WrongSubComponentArgument {
    port
        in Integer speed,
        in MotorCmd command;

  component Motor(30);  // Wrong parameter type expected String
  
  connect speed -> motor.speed;
  connect command -> motor.command;   
}
