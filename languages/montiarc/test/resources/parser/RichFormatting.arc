/* (c) https://github.com/MontiCore/monticore */
package parser;

import java.util.List;
import java.util.Arrays;

component RichFormatting (int threshold, String label = "default") extends Base {
  port in boolean trigger,
       in int value;
  port out int result, status;

  int count = 0;
  int a = 1, b = 2, c = 3;

  component Inner {
    port in int x;
    port out int y;
  }

  Inner inner1;
  Inner inner2, inner3;
  Inner inner4(5);
  Inner inner5(y = threshold);

  value -> inner1.x;
  inner1.y -> inner2.x, inner3.x;

  compute {
    if(value > 0 && value <= 100) {
      result = value * 2 + threshold;
    }
    else {
      result = 0;
    }
  }

  automaton {
    initial state Idle { 
      entry/ {
        count = 0;
      }

    } 
    state Active { 
      entry/ {
        status = 1;
      }
      exit/ {
        status = 0;
      }

    } 
    state Done;

    Idle -> Active [trigger] / {
      List<Integer> values = Arrays.asList(1, 2, 3);
      for (int i = 0; i < 3; i++) {
        count = count + i;
      }
      for (Integer v : values) {
        count = count + v;
      }
    }
    Active -> Idle [!trigger || count == 0] / {
      count = 0;
      status = (count != 0) ? 1 : 0;
      a += 1;
      b -= 2;
      c *= 3;
    }
    Active -> Done;
    Done -> Idle [trigger];
  }
}
