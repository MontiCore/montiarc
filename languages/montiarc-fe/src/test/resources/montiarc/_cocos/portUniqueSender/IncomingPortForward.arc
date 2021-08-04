/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

/**
 * Valid model.
 */
component IncomingPortForward {

  port in Integer i1;
  port in Integer i2;
  port in Integer i3;

  component Inner1 {
    port in Integer i1;
    port in Integer i2;
  }

  component Inner2 {
    port in Integer i1;
    port in Integer i2;
    port in Integer i3;
  }

  Inner1 inner1;
  Inner2 inner2;

  i1 -> inner1.i1;

  i2 -> inner2.i1;
  i2 -> inner2.i3;

  i3 -> inner1.i2;
  i3 -> inner2.i2;
}