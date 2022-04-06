/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// valid
component ValidIncrement(boolean parameter) {
  port in double inPort;
  port out double outPort;

  int variable = 42;

  automaton {
    initial { outPort = 0; } state Begin;
    state End;

    Begin -> End / {++variable; outPort = 0;};
  }
}