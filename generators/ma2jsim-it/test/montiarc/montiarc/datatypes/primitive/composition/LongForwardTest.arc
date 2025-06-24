/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Long;

<<test, testStream=[[1L], [1L, 2L, Long.MAX_VALUE]]>>
component LongForwardTest(List<long> testStream) {
  LongForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<long> generator(testStream);

  AssertEqualsUntimed<long> assert(testStream);
}
