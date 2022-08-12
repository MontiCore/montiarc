/* (c) https://github.com/MontiCore/monticore */
package feedbackLoopTiming;

/*
 * Invalid model.
 */
component FeedbackLoopTimingMismatchComplex {
  component A {
    port in int i;
    port out int o;
  }

  component B {
    A a;
    a.o -> a.i; // feedback loop
  }

  component C {
    port in int i;
    port out int o;
    A a;
    i -> a.i;
    a.o -> o;
  }

  component D {
    C c;
    c.o -> c.i; // feedback loop
  }

  component E {
    C c1, c2;

    c1.o -> c2.i;
    c2.o -> c1.i; // feedback loop
  }

  component F {
    port in int i1, i2;
    port out int o1, o2;

    C c1;

    i1 -> c.i;
    c.o -> o1;

    i2 -> o2;
  }

  component G {
    F f;
    f.o1 -> f.i2; // feedback loop, but actually not -> needs further improvements
  }
}