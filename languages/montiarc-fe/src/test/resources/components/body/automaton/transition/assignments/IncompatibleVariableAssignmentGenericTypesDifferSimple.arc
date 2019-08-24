/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.assignments;

import types.Datatypes.MotorCommand;
import types.Datatypes.TimerSignal;
import types.Datatypes.TimerCmd;
import java.util.HashMap;

/*
 * Invalid model. 
 *
 * @implements [Wor16] AT2: Types of valuations and assignments must match
 *  the type of the assigned input, output, or variable. (p. 105, Lst. 5.24)
 */
component IncompatibleVariableAssignmentGenericTypesDifferSimple {
  
  HashMap<String, Integer> stateChanges;

  automaton BumpControl {
    state Idle;
    initial Idle / {call stateChanges.put(5, "foo")}; //ERROR
    Idle -> Idle / {call stateChanges.put("asd", 4)}; //ERROR
  }
}
