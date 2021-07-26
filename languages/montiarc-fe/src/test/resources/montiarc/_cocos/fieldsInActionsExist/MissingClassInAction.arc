/* (c) https://github.com/MontiCore/monticore */
package fieldsInActionsExist;

/*
 * Invalid model. Class Enum is missing.
 */
component MissingClassInAction {
  port out CMD cmd;

  automaton {
    initial state Begin;
    state End {
      exit / {cmd = CMD.Left;}
    };

    Begin -> End;
  }
}