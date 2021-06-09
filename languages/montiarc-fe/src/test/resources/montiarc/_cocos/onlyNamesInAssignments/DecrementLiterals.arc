/* (c) https://github.com/MontiCore/monticore */
package onlyNamesInAssignments;

// valid
component DecrementLiterals {

  automaton {
    initial state Begin;
    state End;

    Begin -> End / {
      --5;
      4--;
    };
  }
}