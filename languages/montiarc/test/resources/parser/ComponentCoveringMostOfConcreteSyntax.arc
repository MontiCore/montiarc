/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Parsable Model.
 */
component ComponentCoveringMostOfConcreteSyntax {

  port in Integer i1;
  port out String o1, o2, o3;

  i1 -> a1.i1;

  component A {
    port in Integer i1;
    port out Integer o1, o2, o3;
    port out Integer o4, o5, o6;
    port out Integer o7, o8, o9;
  }

  A a1;

  a.o1 -> b1.i1;
  a.o2 -> b1.o2;
  a.o3 -> b1.o3;

  component B b1 {
    port in Integer i1, i2, i3;
    port out Integer o1;
  }

  B b2, b3;

  a.o4 -> b2.i1;
  a.o5 -> b2.o2;
  a.o6 -> b2.o3;
  a.o7 -> b3.i1;
  a.o8 -> b3.o2;
  a.o9 -> b3.o3;

  component C c1, c2, c3 {
    port in Integer i;
    port out String o;
  }

  b1.o1 -> c1.i;
  b2.o1 -> c2.i;
  b3.o1 -> c3.i;
}