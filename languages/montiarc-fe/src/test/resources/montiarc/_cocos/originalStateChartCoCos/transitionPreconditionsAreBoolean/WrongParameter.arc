/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // invalid model
component WrongParameter (String string) {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [string];
  }

}