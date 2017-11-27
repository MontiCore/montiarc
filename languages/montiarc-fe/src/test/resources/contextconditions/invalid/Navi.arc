package contextconditions.invalid;

import contextconditions.valid.Types.*;
import contextconditions.valid.Motor;
 
component Navi {

    port
        in Integer speed,
        in MotorCmd command;

  component Motor(30);  //Wrong parameter type expected String
  
  connect speed -> motor.speed;
  connect command -> motor.command;   
}
