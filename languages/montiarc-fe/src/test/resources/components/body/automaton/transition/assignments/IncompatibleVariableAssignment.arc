/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;
import java.util.HashMap;

/*
 * Invalid model.
 * See comment below.
 *
 * @implements [Wor16] AT2: Types of valuations and assignments must match
 *  the type of the assigned input, output, or variable. (p. 105, Lst. 5.24)
 */
component IncompatibleVariableAssignment {
  port
    in Double distance,
    in TimerSignal signal,
    out TimerCmd timer,
    out MotorCommand right,
    out MotorCommand left;
    
  Integer stateChanges;

  automaton BumpControl {
    state Idle, Driving, Backing, Turning;

    initial Idle / {right = MotorCommand.STOP, left = MotorCommand.STOP};

    //ERRROR 0xMA42 stateChanges (Type java.lang.String can not cast to type Integer)
    Idle -> Driving  / {right = MotorCommand.FORWARD, left = MotorCommand.FORWARD, stateChanges = "1"}; 
    Driving -> Backing [distance < 5] / {right = MotorCommand.BACKWARD, left = MotorCommand.BACKWARD, timer = TimerCmd.SINGLE, stateChanges++}; 
    Backing -> Turning [signal == TimerSignal.ALERT] / {right = MotorCommand.BACKWARD, left = MotorCommand.FORWARD, timer = TimerCmd.DOUBLE};
    Turning -> Driving [signal == TimerSignal.ALERT] / {left = MotorCommand.FORWARD, right = MotorCommand.FORWARD};

  }
}
