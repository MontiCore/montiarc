/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // invalid model
component WrongPort {
  port in double inputPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [inputPort];
  }

}