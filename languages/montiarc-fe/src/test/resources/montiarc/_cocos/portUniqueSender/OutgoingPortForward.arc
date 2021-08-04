/* (c) https://github.com/MontiCore/monticore */
package portUniqueSender;

/**
 * Valid model.
 */
component OutgoingPortForward {

  port out Integer o1;
  port out Integer o2;
  port out Integer o3;

  component Inner1 {
    port out Integer o1;
  }

  component Inner2 {
    port out Integer o1;
    port out Integer o2;
  }

  Inner1 inner1;
  Inner2 inner2;

  inner1.o1 -> o1;
  inner2.o1 -> o2;
  inner2.o2 -> o3;
}