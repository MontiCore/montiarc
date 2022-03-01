/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// invalid, because ++ implies a change to the variable
component IncrementPort(boolean parameter) {
  port in int inPort;
  port out int outPort;

  double variable = 0.001;

  automaton {
    state Begin;
    state End;
    initial Begin / { outPort = 0; };

    Begin -> End [inPort++ == 42 + outPort] / { outPort = 0; };
  }
}