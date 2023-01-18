/* (c) https://github.com/MontiCore/monticore */
package originalStateChartCoCos.transitionPreconditionsAreBoolean;

// valid, because the assignment evaluates to a truth variable
component StrangeAssignment{
  port in boolean inPort;
  port out boolean outPort;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [outPort = true == inPort];
  }
}
