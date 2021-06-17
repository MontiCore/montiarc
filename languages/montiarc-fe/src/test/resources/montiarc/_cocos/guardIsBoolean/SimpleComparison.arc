/* (c) https://github.com/MontiCore/monticore */
package guardIsBoolean;

// valid
component SimpleComparison{
  int variable = 20;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [5 < variable];
  }
}