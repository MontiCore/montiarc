/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

import java.lang.Integer;

/**
 * Valid model.
 */
component PortForwardAndHiddenChannel {

  port in Integer i1,
       out Integer o1;

  port in Integer a,
       out Integer b;

  component Inner1 {
    port in Integer i1,
         out Integer o1;
    port in Integer b,
         out Integer a;
  }

  Inner1 inner1;

  i1 -> inner1.i1;
  inner.o1 -> o1;

  a -> inner1.b;
  inner1.a -> b;
}
