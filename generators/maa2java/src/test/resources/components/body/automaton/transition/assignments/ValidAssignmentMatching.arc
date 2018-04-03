package components.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;

/**
 * Valid model. Assigned value MotorCmd.Forward can only be assigned
 * to port motorCmd.
 */
component ValidAssignmentMatching {
  port 
    out MotorCommand;
  
  automaton AmbiguousMatching {
    state Idle; 
    initial Idle / {MotorCommand.FORWARD};
  }
}