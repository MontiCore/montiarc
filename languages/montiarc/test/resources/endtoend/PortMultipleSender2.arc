/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The outgoing port o is target of multiple connectors.
 */
component PortMultipleSender2 {

  port in int i;
  port out int o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;

  sub.o -> o, o;

}
