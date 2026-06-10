/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: The type of the case expression (boolean) is not compatible
 * with the switch selector type (java.lang.String).
 */
component SwitchCaseIncompatible6 {
  int v = 0;
  automaton {
    initial state S;
    S -> S / {
      switch (v) {
        case "hello": break;
        default: break;
      }
    }
  }
}
