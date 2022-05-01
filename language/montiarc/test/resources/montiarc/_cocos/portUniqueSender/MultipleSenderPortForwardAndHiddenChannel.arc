/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

/**
 * Invalid model.
 */
component MultipleSenderPortForwardAndHiddenChannel {

  port in Integer i1, i2,
       out Integer o1;

  component Inner {
    port in Integer i,
         out Integer o;
  }

  Inner inner1, inner2;

  i1 -> inner1.i;
  i2 -> inner1.i;

  i1 -> inner2.i;
  inner1.o -> inner2.i;

  inner1.o -> o;
  inner2.o -> o;
}
