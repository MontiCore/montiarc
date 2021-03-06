/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // valid model
component JustPort {
  port in boolean inputPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [inputPort];
  }

}