/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// valid
component UsedAllInGuard(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial { outPort = 0; } state Begin;
    state End;

    Begin -> End [parameter == (variable * inPort <= 3)] / { outPort = 0; };
  }
}