/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Valid model: The outgoing port o1 is never used. This should raise a warning.
 */
component PortUnused3 {

  port in int i1, i2;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o2;
  }

  Inner sub;

  i1 -> sub.i1;
  i2 -> sub.i2;

  sub.o2 -> o2;

}
