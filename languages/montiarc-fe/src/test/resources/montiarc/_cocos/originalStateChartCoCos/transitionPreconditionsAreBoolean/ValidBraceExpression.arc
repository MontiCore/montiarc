/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

// valid, because the expression in the braces (and therefore the brace expression itself) evaluates to boolean
component ValidBraceExpression{
  port in int inPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [(5 == inPort)];
  }
}