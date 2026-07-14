/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: The type of the case expression (double) is not compatible
 * with switch expression types (java.lang.Long and long).
 */
component SwitchCaseIncompatible4 {
  Long l1 = 0L;
  long l2 = 0L;
  automaton {
    initial state S;
    S -> S / {
      switch (l1) {
        case 3.14: ;
        default: ;
      }
      switch (l2) {
        case 3.14: ;
        default: ;
      }
    }
  }
}
