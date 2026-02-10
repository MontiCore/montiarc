/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

component Branch2 {
  port in boolean i1, i2;
  port out boolean o1, o2;

  Leaf any;

  mode automaton {
    initial mode M1 {
      Leaf only1;
      Leaf shared;
      only1.o1 -> shared.i1;
      only1.o2 -> shared.i2;
      shared.o1 -> only1.i1;
      shared.o2 -> only1.i2;
    }

    mode M2 {
      Leaf only2;
      Leaf shared;
      only2.o1 -> shared.i1;
      only2.o2 -> shared.i2;
      shared.o1 -> only2.i1;
      shared.o2 -> only2.i2;
    }

      M1 -> M2;
      M2 -> M1;
  }

  i1 -> any.i1;
  i2 -> any.i2;
  any.o1 -> o1;
  any.o2 -> o2;
}
