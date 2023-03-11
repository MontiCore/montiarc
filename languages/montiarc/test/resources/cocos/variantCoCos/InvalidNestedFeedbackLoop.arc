/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidNestedFeedbackLoop(int x) {
  port in int i;
  port out int o;

  component E(int x) {
    port in int i1;
    if (x > 0) {
      port in int i2;
    }
    if (x > 0) {
      if (x > 5) {
        port out int o1;
      } else {
        port <<delayed>> out int o1;
      }
    }
    port out int o2;

    component EE(int x) {
      port in int i1;
      if (x > 0) {
        port in int i2;
        port out int o1;
      }
      port out int o2;
    }

    EE ee(x);
    i1 -> ee.i1;
    if (x > 0) {
      i2 -> ee.i2;
      ee.o1 -> o1;
    }
    ee.o2 -> o2;
  }

  E e(x);
  i -> e.i1;
  if (x > 0) {
    e.o1 -> e.i2;
  }
  e.o2 -> o;
}
