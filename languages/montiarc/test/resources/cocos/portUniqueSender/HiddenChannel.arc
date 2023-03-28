/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

import java.lang.Integer;

/**
 * Valid model.
 */
component HiddenChannel {

  component Inner1 {
    port out Integer o;
    port out Integer o1;
    port out Integer o2;
  }

  component Inner2 {
    port in Integer i;
    port in Integer i1;
    port in Integer i2;
    port in Integer i3;
  }

  component Inner3 {
    port in Integer i1;
  }

  Inner1 inner1;
  Inner2 inner2;
  Inner3 inner3;

  inner1.o -> inner2.i;

  inner1.o1 -> inner2.i2;
  inner1.o1 -> inner2.i3;

  inner1.o2 -> inner2.i1;
  inner1.o2 -> inner3.i1;
}
