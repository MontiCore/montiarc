/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import java.util.List;

<<test, ticks=2, expected=[[[5], [-1], [100, 2]]]>>
component AssertEqualsTimedIntegerTest(List<List<Integer>> expected) {
  component Source source {
    port out Integer o;
    int i = 0;
    automaton {
      initial state S;
      S -> S / {
        if (i==0) o = 5;
        if (i==1) o = -1;
        if (i==2) {
         o = 100;
         o = 2;
        }
        i++;
      }
    }
  }

  AssertEqualsTimed<Integer> equals(expected);
  source.o -> equals.actual;
}
