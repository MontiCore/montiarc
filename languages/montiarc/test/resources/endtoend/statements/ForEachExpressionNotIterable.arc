/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: The type of the expression of the for-each loop is non-iterable.
 */
component ForEachExpressionNotIterable {

  int v = 0;

  automaton {
    initial state S;
    S -> S / {
      for (int i : v) { }
    }
  }
}
