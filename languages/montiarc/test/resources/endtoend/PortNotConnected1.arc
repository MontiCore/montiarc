/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The incoming port i1 of subcomponent sub, defined by inner
 * component Inner, is not connected.
 */
component PortNotConnected1 {

  port in int i2;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o1, o2;
  }

  Inner sub;

  i2 -> sub.i2;

  sub.o1 -> o1;
  sub.o2 -> o2;

}
