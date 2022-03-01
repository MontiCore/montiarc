/* (c) https://github.com/MontiCore/monticore */
package behavior.fieldReadWriteAccessFitsInGuards;

// invalid, because ++ implies a read and a change to the variable
component DeepIncrement(boolean parameter) {
  port in int inPort;
  port out int outPort;

  double variable = 0.001;

  automaton {
    state Begin;
    state End;

    initial Begin / { outPort = 0; };

    Begin -> End [6 + (inPort++ -1-2-3 + outPort++) / 6 == 7 * 6] / { outPort = 0; };
  }
}