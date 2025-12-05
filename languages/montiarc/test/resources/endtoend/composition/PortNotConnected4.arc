/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Valid model: The outgoing port o2 of subcomponent sub, defined by inner
 * component Inner, is not connected. This should raise a warning.
 */
component PortNotConnected4 {

  port in int i1, i2;
  port out int o1;

  component Inner {
    port in int i1, i2;
    port out int o1, o2;
  }

  Inner sub;

  i1 -> sub.i1;
  i2 -> sub.i2;

  sub.o1 -> o1;

}
