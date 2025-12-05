/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The incoming port i2 of subcomponent sub, defined by inner
 * component Inner, is not connected.
 */
component PortNotConnected2 {

  port in int i1;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o1, o2;
  }

  Inner sub;

  i1 -> sub.i1;

  sub.o1 -> o1;
  sub.o2 -> o2;

}
