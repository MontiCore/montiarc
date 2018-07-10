package components.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;
import java.util.HashMap;

/*
 * Invalid model. 
 *
 * @implements TODO
 */
component IncompatibleVariableAssignmentGenericTypesDifferSimple {
  
  HashMap<String, Integer> stateChanges;

  automaton BumpControl {
    state Idle;
    initial Idle / {call stateChanges.put(5, "foo")}; //ERROR
    Idle -> Idle / {call stateChanges.put(4, "asd")}; //ERROR
  }
}