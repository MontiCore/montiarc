package contextconditions.valid;

import contextconditions.valid.Types.*; 
 
component Navi {

    port
        in Integer speed,
        in MotorCmd command;

  component Motor(30);  //Wrong parameter type expected String
  
  connect speed -> motor.speed;
  connect command -> motor.command;   
}
