/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

<<test, ticks=5, value=[true, false], exception=[null, java.lang.AssertionError.class]>>
component EmitBooleanTest(boolean value) {
  Emit<Boolean> emitter(value);

  AssertTrue assert;
  emitter.out -> assert.actual;
}
