/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: The type of the case expression (integer) is not compatible
 * with the switch selector type (java.lang.String).
 */
component SwitchCaseIncompatible5 {
  String text = "hello";
  automaton {
    initial state S;
    S -> S / {
      switch (text) {
        case 100: ;
        default: ;
      }
    }
  }
}
