/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: Variable 'i' is declared multiple times in
 * the same transition action (scope)).
 */
component NoDuplicateVariableDeclarations1 {
  automaton {
    initial state S;
    S -> S / {
      int i = 1;
      int i = 1;
    }
  }
}
