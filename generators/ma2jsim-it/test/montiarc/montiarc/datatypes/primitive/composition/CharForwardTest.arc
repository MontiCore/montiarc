/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Character;

<<test, testStream=[Sync<'a'>, Sync<'a', 'b', Character.MAX_VALUE>]>>
component CharForwardTest(SyncStream<char> testStream) {
  CharForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Character> generator(testStream);

  AssertEqualsUntimed<Character> assert(testStream.untimed());
}
