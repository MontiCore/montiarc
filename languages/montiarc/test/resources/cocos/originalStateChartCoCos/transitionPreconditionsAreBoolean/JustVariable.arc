/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // valid model
component JustVariable {

  boolean free = true;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [free];
  }

}
