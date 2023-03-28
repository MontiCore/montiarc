/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

import java.lang.Integer;

/**
 * Invalid model.
 */
component MultipleSenderIncomingPortForward {

  port in Integer i1, i2;

  component Inner1 {
    port in Integer i1, i2;
  }

  Inner1 inner1;

  i1 -> inner1.i1;
  i1 -> inner1.i2;
  i2 -> inner1.i2;
}
