/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
 * Invalid model: Switch over Enum with numeric case.
 * Numeric literals are not valid case values for enum switch expressions.
 */
component SwitchCaseIncompatible2 {
  OnOff e = OnOff.ON;
  automaton {
    initial state S;
    S -> S / {
      switch (e) {
        case 1: ;
        default: ;
      }
    }
  }
}
