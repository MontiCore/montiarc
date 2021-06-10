/* (c) https://github.com/MontiCore/monticore */
package variableAccessInReactions;

// valid
component UsedAll(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {
      outPort = inPort + variable;
      variable += parameter ? -1 : 1;
    };
  }
}