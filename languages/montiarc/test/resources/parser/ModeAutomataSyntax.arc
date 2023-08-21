/* (c) https://github.com/MontiCore/monticore */
package parser;

component ModeAutomataSyntax {
  port in int i;
  port out int o;

  mode automaton {
    initial mode s1 {
      B b;
      i -> b.i;
      b.o -> o;
    }
    mode s2 {
      C c;
      i -> c.i;
      c.o -> o;
    }
    s1 -> s2;
  }
}
