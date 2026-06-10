/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: The type of the case expression (long) is not compatible
 * with the switch expression type (boolean).
 */
component SwitchCaseIncompatible3 {
  boolean b = false;
  automaton {
    initial state S;
    S -> S / {
      switch (b) {
        case 100L: break;
        default: break;
      }
    }
  }
}
