/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The incoming ports i1 and i2 of subcomponent sub2, defined
 * by inner component InnerInner, of inner component Inner are not connected.
 * The outgoing ports o1 and o2 of the same component are also not connected,
 * however, these should only raise warnings.
 */
component PortNotConnected5 {

  port in int i1, i2;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out int o1, o2;

    component InnerInner {
      port in int i1, i2;
      port out int o1, o2;
    }

    InnerInner sub1, sub2;

    i1 -> sub1.i1;
    i2 -> sub1.i2;

    sub1.o1 -> o1;
    sub1.o2 -> o2;
  }

  Inner sub;

  i1 -> sub.i1;
  i2 -> sub.i2;

  sub.o1 -> o1;
  sub.o2 -> o2;

}
