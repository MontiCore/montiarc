/* (c) https://github.com/MontiCore/monticore */
package variability;

component Variability (int a, int b) {
  feature f1, f2;
  feature f3;

  varif (f1) port <<sync>> in int i1;
  varif (!f1) port <<sync>> in int i2, i3;
  varif (!f2 || f3 || 3 < 2) {
    port <<sync>> in int i2;
    port <<sync>> in int i3, i4;
    port <<sync>> out int o1;
  }

  automaton {
    initial state SomeState;
    SomeState -> SomeState;
  }

  constraint (f1 || f2);
  constraint (!((f2 && !f3) || (!f2 && f3)));
}
