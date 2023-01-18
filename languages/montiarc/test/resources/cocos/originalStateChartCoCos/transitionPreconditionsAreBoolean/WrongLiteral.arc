/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

  // invalid model: 5 can't be casted to boolean
component WrongLiteral {

  automaton {
    initial state Begin;
    state End;

    Begin -> End [5];
  }

}
