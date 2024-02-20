/* (c) https://github.com/MontiCore/monticore */
package automata.dataTypes;

import automata.Datatypes.*;

/**
 * Simple atomic component for testing enums
 */
component EnumComponent {
  port in TimerSignal in;
  port out MotorCmd out;

  <<sync>> automaton{
    initial state Idle;
    state Second;

    Idle -> Idle [in == TimerSignal.ALERT]/{
      out = MotorCmd.FORWARD;
    };

    Idle -> Second [in == TimerSignal.SLEEP]/{
       out = MotorCmd.STOP;
    };
  }
}
