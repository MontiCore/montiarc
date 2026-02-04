/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: Variable 'i' is declared multiple times in
 * the same entry action (scope)).
 */
component NoDuplicateVariableDeclarations2 {
  automaton {
    initial state S {
      entry / {
        int i = 1;
        int i = 1;
      }
    }
  }
}
