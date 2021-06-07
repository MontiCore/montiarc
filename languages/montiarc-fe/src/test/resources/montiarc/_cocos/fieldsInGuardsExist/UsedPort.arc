/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

  // valid model
component UsedPort {
  port in float inputPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [inputPort <= 2.4];
  }

}