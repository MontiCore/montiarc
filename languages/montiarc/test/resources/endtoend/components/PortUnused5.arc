/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Valid model: The ports of inner component Inner are never used. This should
 * raise warnings.
 */
component PortUnused5 {

  port in int i1, i2;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o1, o2;

    component InnerInner sub { }

  }

  Inner sub;

  i1 -> sub.i1;
  i2 -> sub.i2;

  sub.o1 -> o1;
  sub.o2 -> o2;

}
