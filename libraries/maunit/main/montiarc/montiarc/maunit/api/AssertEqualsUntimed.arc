/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.maunit.api.Assertions;
import java.util.List;

component AssertEqualsUntimed<T>(List<T> expected, String message = "") {
  port in T actual;

  int index = 0;

  automaton {
    initial state S;
    S -> S actual / {
      if (index >= expected.size()) Assertions.fail("Unexpected additional message received with value: " + actual);
      Assertions.assertTrue(expected.get(index) == actual, message);
      index++;
    }
  }
}
