package components.body.ajava;

import types.Datatypes.MotorCommand;

component ComputeWithSwitchCaseStatement {

  compute {
    MotorCommand m = MotorCommand.FORWARD;
    
    switch(m) {
      case FORWARD:
        break;
      default: 
        break;
    }   
  }
}