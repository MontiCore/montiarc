/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // valid model
component WrongVariable {

  int wrong = 4 + 2;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [wrong];
  }

}
