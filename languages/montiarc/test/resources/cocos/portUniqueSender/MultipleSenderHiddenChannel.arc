/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

/**
 * Invalid model.
 */
component MultipleSenderHiddenChannel {

  component Inner1 {
    port out Integer o1;
    port out Integer o2;
  }

  component Inner2 {
    port in Integer i1;
    port in Integer i2;
  }

  Inner1 inner1;
  Inner2 inner2;

  inner1.o1 -> inner2.i1;
  inner1.o1 -> inner2.i2;
  inner1.o2 -> inner2.i2;
}
