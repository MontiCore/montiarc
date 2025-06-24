/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Double;

<<test, testStream=[[1.5], [1.5, 2.25, Double.MAX_VALUE, Double.MIN_NORMAL, Double.POSITIVE_INFINITY, Double.NaN, -0.0]]>>
component DoubleForwardTest(List<double> testStream) {
  DoubleForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Double> generator(testStream);

  AssertEqualsUntimed<Double> assert(testStream);
}
