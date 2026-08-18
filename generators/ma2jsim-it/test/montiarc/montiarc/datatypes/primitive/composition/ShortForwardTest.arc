/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Short;

<<test, testStream=[Sync<Short.MIN_VALUE>, Sync<Short.MIN_VALUE, Short.MAX_VALUE>]>>
component ShortForwardTest(SyncStream<short> testStream) {
  ShortForward sut;

  generator.out -> sut.pIn;
  sut.pOut -> assert.actual;

  EmitSync<Short> generator(testStream);

  AssertEqualsUntimed<Short> assert(testStream.untimed());
}
