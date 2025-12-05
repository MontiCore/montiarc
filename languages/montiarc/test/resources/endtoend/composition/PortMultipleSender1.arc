/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The outgoing port o is target of multiple connectors.
 */
component PortMultipleSender1 {

  port in int i;
  port out int o;

  component Inner {
    port in int i;
    port out int o1, o2;
  }

  Inner sub;

  i -> sub.i;

  sub.o1 -> o;
  sub.o2 -> o;

}
