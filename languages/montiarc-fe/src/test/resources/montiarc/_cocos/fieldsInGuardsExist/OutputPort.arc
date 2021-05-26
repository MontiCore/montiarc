/* (c) https://github.com/MontiCore/monticore */
package fieldsInGuardsExist;

// invalid, because the values of output ports cannot be readed
component OutputPort {
  port out boolean outputPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [outputPort];
  }
}