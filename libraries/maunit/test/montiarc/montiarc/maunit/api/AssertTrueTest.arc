/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

<<test, ticks=5, value=[true, false], exception=[null, java.lang.AssertionError.class]>>
component AssertTrueTest(boolean value) {
  component Source(boolean value) source(value) {
    port out boolean o;
    compute {
      o = value;
    }
  }

  AssertTrue assert;
  source.o -> assert.actual;
}
