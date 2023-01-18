/* (c) https://github.com/MontiCore/monticore */
package subPortsConnected;

/**
 * Invalid model. Outgoing ports of subcomponents need connection (inner1.o2, inner2.o2).
 */
component SubOutgoingPortNotConnected {

  port in int i1;

  port out int o1;

  component Inner1 {
    port in int i1;
    port out int o1;
    port out int o2;
  }

  component Inner2 {
    port in int i1;
    port out int o1;
    port out int o2;
  }

  Inner1 inner1;
  Inner2 inner2;

  i1 -> inner1.i1;
  inner1.o1 -> inner2.i1;
  inner2.o1 -> o1;
}
