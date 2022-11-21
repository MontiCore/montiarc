/* (c) https://github.com/MontiCore/monticore */
package portsConnected;

/**
 * Invalid model. Outgoing ports need connection (port o2).
 */
component OutgoingPortNotConnected {

  port in int i1;
  port in int i2;

  port out int o1;
  port out int o2;

  component Inner1 {
    port in int i1;
    port in int i2;
    port out int o1;
    port out int o2;
  }

  component Inner2 {
    port in int i1;
    port in int i2;
    port out int o1;
  }

  Inner1 inner1;
  Inner2 inner2;

  i1 -> inner1.i1;
  i2 -> inner1.i2;
  inner1.o1 -> inner2.i1;
  inner1.o2 -> inner2.i2;
  inner2.o1 -> o1;
}