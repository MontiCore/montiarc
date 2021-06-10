/* (c) https://github.com/MontiCore/monticore */
package variableAccessInReactions;

// valid
component SerialAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  double doubleVariable = 4.2;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {outPort = doubleVariable = inPort;};
  }
}