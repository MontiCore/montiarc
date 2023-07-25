/* (c) https://github.com/MontiCore/monticore */
package variability;

component VariabilityWithInner(int a, int b) {
  feature f1, f2;
  feature f3;

  varif (f1) port in int i1;
  varif (!f1) port in int i2, i3;
  varif (!f2 || f3 || 3 < 2) {
    port in int i2;
    port in int i3, i4,
         out int o1;

    component Inner {
      feature f4;
      port in int i;
      port out int o;
    }

    Inner inner;

    inner.o -> o1;
  }

  constraint (f1 || f2);
  constraint (!((f2 && !f3) || (!f2 && f3)));
}
