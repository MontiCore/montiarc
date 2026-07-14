/* (c) https://github.com/MontiCore/monticore */
package statements;

component SwitchCaseIncompatible1 {
  boolean b = false;
  automaton {
    initial state S;
    S -> S / {
      switch (b) {
        case 1: ;
        default: ;
      }
    }
  }
}
