/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;

component AssertEquals<T>(T expected, String message = "") {
  port in T actual;

  <<timed>> automaton {
    initial state S;
    S -> S actual / {
      Assertions.assertTrue(expected == actual, message);
    };
  }
}
