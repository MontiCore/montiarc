/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;
import java.util.List;

component AssertEqualsSync<T>(List<T> expected, String message = "") {
  port sync in T actual;

  int index = 0;

  automaton {
    initial state S;
    S -> S / {
      if (index >= expected.size()) Assertions.fail("Unexpected additional message received with value: " + actual);
      Assertions.assertTrue(expected.get(index) == actual, message);
      index++;
    }
  }
}
