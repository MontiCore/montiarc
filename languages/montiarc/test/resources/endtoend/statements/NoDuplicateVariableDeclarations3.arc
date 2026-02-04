/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: Variable 'i' is declared multiple times in
 * the same exit action (scope)).
 */
component NoDuplicateVariableDeclarations3 {
  automaton {
    initial state S {
      exit / {
        int i = 1;
        int i = 1;
      }
    }
  }
}
