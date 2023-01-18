/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

// invalid, because the expression in the braces (and therefore the brace expression itself) evaluates to int
component InvalidBraceExpression{
  port in int inPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [(5 + inPort)];
  }
}
