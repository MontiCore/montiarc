/* (c) https://github.com/MontiCore/monticore */
package onlyNamesInAssignments;

// valid
component IncrementAnAddition {

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {
      ++(4 + 5);
    };
  }
}