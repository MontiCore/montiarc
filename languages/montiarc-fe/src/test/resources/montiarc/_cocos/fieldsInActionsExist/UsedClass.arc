/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// valid
component UsedClass() {
  port out String s;

  automaton {
    initial state Begin;
    state End;

    Begin -> End / { s = String.format("a");};
  }
}