/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The incoming port i of subcomponent sub, defined by inner
 * component Inner, is target of multiple connectors.
 */
component PortMultipleSender3 {

  port in int i1, i2;
  port out int o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i1 -> sub.i;
  i2 -> sub.i;

  sub.o -> o;

}
