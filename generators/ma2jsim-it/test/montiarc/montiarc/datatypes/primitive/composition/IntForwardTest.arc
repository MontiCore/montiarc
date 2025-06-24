/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;
import java.lang.Integer;

<<test, testStream=[[1], [1, 2, Integer.MAX_VALUE]]>>
component IntForwardTest(List<int> testStream) {
  IntForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Integer> generator(testStream);

  AssertEqualsUntimed<Integer> assert(testStream);
}
