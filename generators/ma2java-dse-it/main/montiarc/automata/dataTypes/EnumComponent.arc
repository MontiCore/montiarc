/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

import automata.Datatypes.*;

/**
 * Simple atomic component for testing enums
 */
component EnumComponent {
  port <<sync>> in TimerSignal in;
  port <<sync>> out MotorCmd out;

  automaton{
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
