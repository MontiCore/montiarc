/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

import types.Datatypes.MotorCommand;

/*
 * Valid model.
 */
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