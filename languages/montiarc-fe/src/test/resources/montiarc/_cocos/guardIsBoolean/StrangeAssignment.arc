/* (c) https://github.com/MontiCore/monticore */
package guardIsBoolean;

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