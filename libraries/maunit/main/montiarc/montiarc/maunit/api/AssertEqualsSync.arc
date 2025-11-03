/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;

component AssertEqualsSync<T>(SyncStream<T> expected, String message = "") {
  port sync in T actual;

  SyncStream<T> remaining = expected;

  automaton {
    initial state S;
    S -> S / {
      if (remaining.isEmpty()) {
        Assertions.fail("Unexpected additional message");
      }
      Assertions.assertTrue(remaining.first() == actual, message);
      remaining = remaining.dropFirst();
    }
  }
}
