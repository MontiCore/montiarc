/* (c) https://github.com/MontiCore/monticore */
package variableAccessInGuards;

// invalid, because the value of outPort is unknown
component ReadOutgoingPort(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [outPort < 5];
  }
}