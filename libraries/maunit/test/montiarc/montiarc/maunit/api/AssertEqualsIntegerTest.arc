/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

<<test, ticks=5, value=[5, -1], exception=[null, java.lang.AssertionError.class]>>
component AssertEqualsIntegerTest(int value) {
  component Source source {
    port out Integer o;
    <<timed>> compute {
      o = 5;
    }
  }

  AssertEquals<Integer> equals(value);
  source.o -> equals.actual;
}
