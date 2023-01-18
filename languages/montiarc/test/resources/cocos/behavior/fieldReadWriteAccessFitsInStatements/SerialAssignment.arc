/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInStatements;

// valid
component SerialAssignment(boolean parameter) {
  port in double inPort;
  port out double outPort;

  double doubleVariable = 4.2;

  automaton {
    initial { outPort = 0; } state Begin;
    state End;

    Begin -> End / {outPort = doubleVariable = inPort;};
  }
}
