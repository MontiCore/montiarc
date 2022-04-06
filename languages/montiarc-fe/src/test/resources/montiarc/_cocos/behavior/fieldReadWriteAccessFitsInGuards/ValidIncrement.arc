/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// valid
component ValidIncrement(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial { outPort = 0; } state Begin;
    state End;

    Begin -> End [++variable > 100] / { outPort = 0; };
  }
}