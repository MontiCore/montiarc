/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// valid
component UsedVariablePortAndParameter(double parameter) {
  port in boolean inPort,
       out boolean outPort;

  int variable = 5;

  automaton {
    initial state Begin;
    state End {
      entry / {outPort = inPort;}
      exit / {variable = variable * 20;}
    };

    Begin -> End / {outPort = parameter <= 11.4;};
  }
}