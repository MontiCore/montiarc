/* (c) https://github.com/MontiCore/monticore */
package montiarc.ma2jsim.test;

component Variability (int a, int b) {
  feature f1, f2;
  feature f3;

  varif (f1) port <<sync>> in int i1;
  varif (!f1) port <<sync>> in boolean i1, i2;
  varif (!f2 || f3 || 3 < 2) {
    port <<sync>> in int i3, i4;
    port <<sync>> out int o1;
  }

  <<sync>> automaton {
    initial state SomeState;
    SomeState -> SomeState;
  }

  constraint (f1 || f2);
  constraint (!((f2 && !f3) || (!f2 && f3)));
}
