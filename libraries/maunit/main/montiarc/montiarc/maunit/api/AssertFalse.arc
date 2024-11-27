/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;

component AssertFalse(String message = "") {
  port in boolean actual;

  <<timed>> automaton {
    initial state S;
    S -> S actual / {
      Assertions.assertFalse(actual, message);
    };
  }
}
