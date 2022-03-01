/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// invalid, cannot write to inPort
component InvalidAssignment(boolean parameter) {
  port in double inPort1, inPort2;
  port out double outPort;

  int variable = 42;

  automaton {
    state Begin;
    state End;
    initial Begin / { outPort1 = 0; outPort2 = 0; };

    Begin -> End [(inPort1 = 5.7 + inPort2++) == 5.7] / { outPort1 = 0; outPort2 = 0; };
  }
}