/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // valid model
component JustParameter (boolean free) {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [free];
  }

}
