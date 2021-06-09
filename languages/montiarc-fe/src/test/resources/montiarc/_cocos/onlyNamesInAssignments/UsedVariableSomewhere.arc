/* (c) https://github.com/MontiCore/monticore */
package onlyNamesInAssignments;

// valid
component UsedVariableSomewhere {
  int variable = 7;

  automaton {
    initial state Begin;
    state End;

    Begin -> End [(variable += 4 - variable * 4 <9) == false];
  }
}