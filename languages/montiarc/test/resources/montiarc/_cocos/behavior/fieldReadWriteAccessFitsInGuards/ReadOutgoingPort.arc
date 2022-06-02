/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// invalid, because the value of outPort is unknown
component ReadOutgoingPort(boolean parameter) {
  port in double inPort;
  port out double outPort1, outPort2;

  int variable = 42;

  automaton {
    initial { outPort1 = 0; outPort2 = 0; } state Begin;
    state End;

    Begin -> End [outPort1 < 5 + outPort2] / { outPort1 = 0; outPort2 = 0; };
  }
}