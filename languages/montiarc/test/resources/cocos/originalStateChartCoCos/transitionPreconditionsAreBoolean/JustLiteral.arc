/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // valid model
component JustLiteral {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [false];
  }

}