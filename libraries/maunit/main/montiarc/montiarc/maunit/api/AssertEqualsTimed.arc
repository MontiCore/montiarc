/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;

component AssertEqualsTimed<T>(EventStream<T> expected, String message = "") {
  port in T actual;

  UntimedStream<T> remainingTick = expected.isEmpty() ? Untimed<T><> : expected.first();
  EventStream<T> remaining = expected.isEmpty() ? expected : expected.dropFirst();

  automaton {
    initial state S;
    S -> S / {
      if (!remainingTick.isEmpty()) {
        Assertions.fail("Not all messages received in time slice");
      }
      if (remaining.isEmpty()) {
        Assertions.fail("Unexpected additional tick");
      }
      remainingTick = remaining.first();
      remaining = remaining.dropFirst();
    }
    S -> S actual / {
      if (remainingTick.isEmpty()) Assertions.fail("Unexpected additional message received with value: " + actual);
      Assertions.assertTrue(remainingTick.first() == actual, message);
      remainingTick = remainingTick.dropFirst();
    }
  }
}
