/* (c) https://github.com/MontiCore/monticore */
package onlyNamesInAssignments;

// valid
component AssignToIncrement {
  int variable = 42;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {
      variable++ = 7;
    };
  }
}