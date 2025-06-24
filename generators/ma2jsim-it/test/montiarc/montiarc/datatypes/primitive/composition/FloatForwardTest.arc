/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Float;

<<test, testStream=[[1.5f], [1.5f, 2.25f, Float.MAX_VALUE, Float.MIN_NORMAL, Float.POSITIVE_INFINITY, Float.NaN, -0.0f]]>>
component FloatForwardTest(List<float> testStream) {
  FloatForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Float> generator(testStream);

  AssertEqualsUntimed<Float> assert(testStream);
}
