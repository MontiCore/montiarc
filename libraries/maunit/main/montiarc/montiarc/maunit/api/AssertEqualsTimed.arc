/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;
import java.util.List;

component AssertEqualsTimed<T>(List<List<T>> expected, String message = "") {
  port in T actual;

  int tick = 0;
  int index = 0;

  <<timed>> automaton {
    initial state S;
    S -> S / {
      if (tick >= expected.size()) {
        Assertions.fail("Unexpected additional tick: " + tick);
      }
      if (index < expected.get(tick).size()) {
        Assertions.fail("Not all expected messages received in tick: " + tick);
      }
      tick++;
      index = 0;
    };
    S -> S actual / {
      if (tick >= expected.size() || index >= expected.get(tick).size()) {
        Assertions.fail("Unexpected additional message received in tick: " + tick + " with value: " + actual);
      }
      Assertions.assertTrue(expected.get(tick).get(index) == actual, message);
      index++;
    };
  }
}
