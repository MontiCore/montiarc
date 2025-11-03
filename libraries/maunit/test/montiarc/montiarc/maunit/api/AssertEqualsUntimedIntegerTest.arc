/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import java.util.List;

<<test={[Untimed<5, -1, 100, 2>]}, ticks=3>>
component AssertEqualsUntimedIntegerTest(UntimedStream<Integer> values) {
  component Source source {
    port out Integer o;
    int i = 0;
    compute {
      if (i==0) o = 5;
      if (i==1) o = -1;
      if (i==2) {
       o = 100;
       o = 2;
      }
      i++;
    }
  }

  AssertEqualsUntimed<Integer> equals(values);
  source.o -> equals.actual;
}
