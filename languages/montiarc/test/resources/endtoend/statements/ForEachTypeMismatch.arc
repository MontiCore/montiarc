/* (c) https://github.com/MontiCore/monticore */
package statements;

import java.util.List;

import java.util.Arrays;

/**
 * Invalid model: The iterable type of the for-each loop is not compatible
 * with the type of the loop variable.
 */
component ForEachTypeMismatch {

  List<Boolean> v = Arrays.asList(true);

  automaton {
    initial state S;
    S -> S / {
      for (Integer i : v) { }
    }
  }
}
