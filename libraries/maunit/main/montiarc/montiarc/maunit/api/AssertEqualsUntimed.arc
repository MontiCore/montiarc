/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;

component AssertEqualsUntimed<T>(UntimedStream<T> expected, String message = "") {
  port in T actual;

  UntimedStream<T> remaining = expected;

  automaton {
    initial state S;
    S -> S actual / {
      if (remaining.isEmpty()) Assertions.fail("Unexpected additional message received with value: " + actual);
      Assertions.assertTrue(remaining.first() == actual, message);
      remaining = remaining.dropFirst();
    }
  }
}
