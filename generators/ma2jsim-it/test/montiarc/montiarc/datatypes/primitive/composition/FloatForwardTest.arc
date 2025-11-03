/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Float;

<<test, testStream=[Sync<1.5f>, Sync<1.5f, 2.25f, Float.MAX_VALUE, Float.MIN_NORMAL, Float.POSITIVE_INFINITY, Float.NaN, -0.0f>]>>
component FloatForwardTest(SyncStream<float> testStream) {
  FloatForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Float> generator(testStream);

  AssertEqualsUntimed<Float> assert(testStream.untimed());
}
