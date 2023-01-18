/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

/**
 * Invalid model.
 */
component MultipleSenderOutgoingPortForward {

  port out Integer o1, o2;

  component Inner1 {
    port out Integer o1, o2;
  }

  Inner1 inner1;

  inner1.o1 -> o1;
  inner1.o1 -> o2;
  inner1.o2 -> o2;
}
