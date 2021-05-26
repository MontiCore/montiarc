/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

// valid
component UsedParameter(int parameter) {

  automaton {
    initial state Begin;
    state End;

    Begin -> End /{parameter = parameter + 1;};
  }
}