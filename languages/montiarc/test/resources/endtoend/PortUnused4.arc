/* (c) https://github.com/MontiCore/monticore */

/*
 * Valid model: The outgoing port o2 is never used. This should raise a warning.
 */
component PortUnused4 {

  port in int i1, i2;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o1;
  }

  Inner sub;

  i1 -> sub.i1;
  i2 -> sub.i2;

  sub.o1 -> o1;

}
